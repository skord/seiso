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

import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Map;

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.exception.ResourceNotFoundException;
import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.dto.MapItemDto;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION)
@XSlf4j
@Transactional
public class ItemSearchController {
	@Autowired private ItemMetaLookup itemMetaLookup;
	@Autowired private Repositories repositories;
	@Autowired private ConversionService conversionService;
	@Autowired private ItemAssembler itemAssembler;
	
	// FIXME Temporarily hand-coding this. See below.
	@Autowired private NodeRepo nodeRepo;
	
	@RequestMapping(
			value = "/{repoKey}/search/{search}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PagedResources<MapItemDto> search(
			@PathVariable String repoKey,
			@PathVariable String search,
			WebRequest request) {
		
		log.trace("Searching: repoKey={}, search={}", repoKey, search);
		val itemClass = itemMetaLookup.getItemClass(repoKey);
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val items = search(itemClass, search, request.getParameterMap());
		val itemPage = new PageImpl(items);
		val projectionNode = itemMeta.getProjectionNode(Projection.Cardinality.COLLECTION, Projection.DEFAULT);
		return itemAssembler.toDtoPage(itemPage, projectionNode);
	}
	
	// FIXME For now we return a List, but we need to have a way to handle pagination too. [WLW]
	// FIXME Also need to be able to handle single return values. Like finding a machine by IP address. See
	// ItemPropertyController for an example. [WLW]
	// FIXME Some of this probably belongs in ItemServiceImpl. Need to think about this a bit. [WLW]
	@SuppressWarnings("unchecked")
	private <T extends Item> List<T> search(Class<?> itemClass, String search, Map<String, String[]> paramMap) {
		val itemMeta = itemMetaLookup.getItemMeta(itemClass);
		val method = itemMeta.getRepositorySearchMethod(search);
		notNull(method, "Unknown search: " + search);
		
		log.trace("Finding {} using method {}", itemClass.getSimpleName(), method.getName());
		val repo = repositories.getRepositoryFor(itemClass);
		val paramClasses = method.getParameterTypes();
		val allAnns = method.getParameterAnnotations();
		val paramVals = new Object[paramClasses.length];
		for (int i = 0; i < paramClasses.length; i++) {
			log.trace("Processing param {}", i);
			val currentAnns = allAnns[i];
			for (val currentAnn : currentAnns) {
				if (Param.class.equals(currentAnn.annotationType())) {
					log.trace("Found @Param");
					if (conversionService.canConvert(String.class, paramClasses[i])) {
						val paramAnn = (Param) currentAnn;
						val paramName = paramAnn.value();
						val paramValAsStr = paramMap.get(paramName)[0];
						log.trace("Setting param: {}={}", paramName, paramValAsStr);
						paramVals[i] = conversionService.convert(paramValAsStr, paramClasses[i]);
					} else {
						log.trace("BUG! Not setting the param value!");
					}
				}
			}
		}
		
		log.trace("Invoking method {} on repo {} with {} params", method.getName(), repo.getClass(), paramVals.length);
		// FIXME ClassCastException when this isn't a list. E.g. EndpointRepo.findByIpAddressAndPort. [WLW]
		val result = (List<T>) ReflectionUtils.invokeMethod(method, repo, paramVals);
		log.trace("Found {} results", result.size());
		return result;
	}
	
	// Hand-coding this one because we don't want to return PagedResources anymore, and don't want camel-case params,
	// but also don't want to break existing code. Make this one the way we want it to be and then convert the other one
	// over in coordination with Eos. Anyway in this case it's a single node we're returning, not a page. [WLW]
	@RequestMapping(
			value = "/nodes/search/find-by-ip-address-and-port",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public MapItemDto findNodeByIpAddressAndPort(
			@RequestParam("ip-address") String ipAddress,
			@RequestParam("port") Integer port) {
		
		val nodes = nodeRepo.findByIpAddressAndPort(ipAddress, port);
		
		if (nodes.isEmpty()) {
			throw new ResourceNotFoundException("No such node: ipAddress=" + ipAddress + ", port=" + port);
		} else if (nodes.size() > 1) {
			throw new RuntimeException("Found " + nodes.size() + " nodes but expected only 1");
		}
		
		val node = nodes.get(0);
		val nodeMeta = itemMetaLookup.getItemMeta(Node.class);
		val projectionNode = nodeMeta.getProjectionNode(Projection.Cardinality.SINGLE, Projection.DEFAULT);
		return itemAssembler.toDto(node, projectionNode);
	}
}
