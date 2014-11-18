/* 
 * Copyright 2013-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.seiso.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.exception.ResourceNotFoundException;
import com.expedia.seiso.core.util.C;
import com.expedia.seiso.core.util.PageMetadataUtils;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.meta.RepoMeta;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.domain.service.response.SaveAllResponse;
import com.expedia.seiso.web.ResponseHeaders;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.PageLinks;
import com.expedia.seiso.web.dto.MapItemDto;
import com.expedia.seiso.web.dto.PEItemDto;
import com.expedia.seiso.web.dto.PEItemDtoList;

// TODO Look into supporting HTTP PATCH and PATCH with JSON:
// http://www.mnot.net/blog/2012/09/05/patch
// http://tools.ietf.org/html/rfc5789
// http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10

// TODO For now, putting @Transactional here because item assembly requires it. If we want to move it to ItemAssembler
// (and I think we do) then we will want to have an ItemAssembler interface and implementation class. [WLW]

/**
 * Default controller to handle CRUD (create, read, update, delete) operations on top-level items.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION)
@XSlf4j
public class ItemController {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private ItemService itemService;
	@Autowired private ItemAssembler itemAssembler;
	@Autowired private EntityLinks entityLinks;
	@Autowired private PageLinks pageLinks;
	
	/**
	 * Handles both paging and non-paging repositories, since we don't know in advance of processing the request whether
	 * the relevant repository is paging.
	 * 
	 * @param repoKey
	 *            repository key
	 * @param pageable
	 *            page request
	 * @return items
	 */
	@Transactional
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HttpEntity<?> getPage(
			@PathVariable String repoKey,
			@RequestParam(value = "view", defaultValue = Projection.DEFAULT) String projectionName,			
			@PageableDefault(
					page = C.DEFAULT_PAGE_NUMBER,
					size = C.DEFAULT_PAGE_SIZE,
					direction = Direction.ASC)
			Pageable pageable) {
		
		log.trace("Getting items: /{}", repoKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		
		if (itemMeta == null) {
			throw new ResourceNotFoundException("Can't find resource " + repoKey);
		}
		
		val projectionNode = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, projectionName);
		
		if (!itemMeta.isPagingRepo()) {
			
			// Body
			val items = itemService.findAll(itemClass);
			val dtoList = itemAssembler.toDtoList(items, projectionNode);
			
			// Headers
			val responseHeaders = new HttpHeaders();
			val selfLink = entityLinks.linkFor(itemClass).withRel(Link.REL_SELF);
			responseHeaders.add(ResponseHeaders.LINK, selfLink.toString());
			responseHeaders.add(ResponseHeaders.X_SELF, selfLink.getHref());
			
			return new HttpEntity<List>(dtoList, responseHeaders);
			
		} else {
			
			// We have a paging repository.
			val entityPage = itemService.findAll(itemClass, pageable);
			val dtoPage = itemAssembler.toDtoPage(entityPage, projectionNode);
			
			// TODO Move this stuff to the assembler. We should still build the PagedResources. That doesn't mean we
			// have to serialize it as a PagedResources. [WLW]
			val pageMeta = dtoPage.getMetadata();
			val totalElems = pageMeta.getTotalElements();
			val totalPages = PageMetadataUtils.getCorrectedTotalPages(pageMeta);
			val dtoCollection = dtoPage.getContent();
			
			// Hm, PagedResources has getPreviousLink() and getNextLink(). But it doesn't have first/last, which we
			// need. [WLW]
			// FIXME Include the projection name in the self link. [WLW]
			val selfLink = entityLinks.linkFor(itemClass).withRel(Link.REL_SELF);
			val firstLink = pageLinks.firstLink(repoKey, projectionName, pageMeta);
			val prevLink = pageLinks.prevLink(repoKey, projectionName, pageMeta);
			val nextLink = pageLinks.nextLink(repoKey, projectionName, pageMeta);
			val lastLink = pageLinks.lastLink(repoKey, projectionName, pageMeta);
			
			val responseHeaders = new HttpHeaders();
			
			// RFC 5988: http://tools.ietf.org/html/rfc5988
			val links = new ArrayList<Link>();
			links.add(selfLink);
			links.add(firstLink);
			if (prevLink != null) { links.add(prevLink); }
			if (nextLink != null) { links.add(nextLink); }
			links.add(lastLink);
			responseHeaders.add(ResponseHeaders.LINK, links.toString());
			
			// Extension headers
			responseHeaders.add(ResponseHeaders.X_SELF, selfLink.getHref());
			responseHeaders.add(ResponseHeaders.X_PAGINATION_FIRST, firstLink.getHref());
			if (prevLink != null) {
				responseHeaders.add(ResponseHeaders.X_PAGINATION_PREV, prevLink.getHref());
			}
			if (nextLink != null) {
				responseHeaders.add(ResponseHeaders.X_PAGINATION_NEXT, nextLink.getHref());
			}
			responseHeaders.add(ResponseHeaders.X_PAGINATION_LAST, lastLink.getHref());
			responseHeaders.add(ResponseHeaders.X_PAGINATION_TOTAL_ELEMENTS, String.valueOf(totalElems));
			responseHeaders.add(ResponseHeaders.X_PAGINATION_TOTAL_PAGES, String.valueOf(totalPages));
			
			return new HttpEntity<Collection>(dtoCollection, responseHeaders);
		}
	}
	
	@Transactional
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@SuppressWarnings("rawtypes")
	public MapItemDto getOne(
			@PathVariable("repoKey") RepoMeta repoMeta,
			@PathVariable String itemKey,
			@RequestParam(value = "view", defaultValue = Projection.DEFAULT) String projectionName) {
		
		val itemClass = repoMeta.getItemClass();
		val item = itemService.find(new SimpleItemKey(itemClass, itemKey));
		
		// TODO Use resolver here too
		val itemTypeMeta = itemMetaLookup.getItemMeta(itemClass);
		val projectionNode = itemTypeMeta.getProjectionNode(Projection.Cardinality.SINGLE, projectionName);
		
		return itemAssembler.toDto(item, projectionNode);
	}
	
	@RequestMapping(
			value = "/{repoKey}",
			method = RequestMethod.POST,
			params = "mode=batch",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public SaveAllResponse postAll(@PathVariable String repoKey, PEItemDtoList peItemDtos) {
		log.trace("Batch saving {} items: repoKey={}", peItemDtos.size(), repoKey);
		
		// FIXME The SaveAllResponse contains a SaveAllError, which in turn contains an Item. If the Item has a cycle,
		// then JSON serialization results in a stack overflow exception. [WLW]
		// 
		// See http://stackoverflow.com/questions/10065002/jackson-serialization-of-entities-with-birectional-relationships-avoiding-cyc
		// for a possible solution. But do we really want to leave it up to Jackson to decide on the serialized
		// representation, when in general we control that ourselves? We should be assembling a DTO here, or else just
		// returning ID info. [WLW]
		// 
		// http://www.cowtowncoder.com/blog/archives/2012/03/entry_466.html [WLW]
		return itemService.saveAll(peItemDtos);
	}
	
	/**
	 * Handles HTTP PUT requests against top-level resources. Following HTTP semantics, this creates the resource if it
	 * doesn't already exist; otherwise, it completely replaces the existing resource (as opposed to merging it). With
	 * respect to the latter, null and missing fields in the incoming representation map to null fields in the
	 * persistent resource.
	 * 
	 * @param itemDto
	 *            item to save
	 */
	@RequestMapping(
			value = "/{repoKey}/{itemKey}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(
			@PathVariable String repoKey,
			@PathVariable String itemKey,
			PEItemDto itemDto) {
		
		log.trace("Putting item: repoKey={}, itemKey={}", repoKey, itemKey);
		itemService.save(itemDto.getItem());
	}
	
	@RequestMapping(value = "/{repoKey}/{itemKey}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SuppressWarnings("rawtypes")
	public void delete(@PathVariable String repoKey, @PathVariable String itemKey) {
		log.info("Deleting item: /{}/{}", repoKey, itemKey);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		itemService.delete(new SimpleItemKey(itemClass, itemKey));
	}
}
