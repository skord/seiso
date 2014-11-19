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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.repo.search.SearchEngine;
import com.expedia.seiso.domain.repo.search.query.token.TokenizedSearchQuery;
import com.expedia.seiso.domain.repo.search.query.token.ValidationAnnotation;
import com.expedia.seiso.domain.repo.search.query.token.Validator;
import com.expedia.seiso.web.dto.MapItemDto;

/**
 * @author Ken Van Eyk (kvaneyk@expedia.com)
 */
@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION)
@XSlf4j
public class SearchController {
	private SearchEngine<? extends Item, ? extends Serializable> searchEngine;

	@Autowired
	public SearchController(@NotNull SearchEngine<? extends Item, ? extends Serializable> searchEngine) {
		this.searchEngine = searchEngine;
	}

	private Map<String, List<MapItemDto>> convertPagedSearchResultsToNonPagedSearchResults(
			Map<String, PagedResources<MapItemDto>> pagedSearchResults) {
		Map<String, List<MapItemDto>> searchResults = new LinkedHashMap<String, List<MapItemDto>>();

		for (Map.Entry<String, PagedResources<MapItemDto>> pagedSearchResultsEntry : pagedSearchResults.entrySet()) {
			searchResults.put(pagedSearchResultsEntry.getKey(), new ArrayList<MapItemDto>(pagedSearchResultsEntry
					.getValue().getContent()));
		}

		return searchResults;
	}

	@Transactional
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, List<MapItemDto>> search(
			@Valid @ModelAttribute("query") @ValidationAnnotation TokenizedSearchQuery tokenizedSearchQuery,
			BindingResult bindingResult) throws InstantiationException, IllegalAccessException, InterruptedException,
			ExecutionException {
		Map<String, List<MapItemDto>> searchResults = null;

		String searchQuery = (tokenizedSearchQuery == null ? null : tokenizedSearchQuery.getQuery());
		log.trace("search({}}", searchQuery);

		// TODO validator is not being invoked on search method call, so explicitly doing it here for now
		if (!new Validator().isValid(tokenizedSearchQuery, null)) {
			throw new RuntimeException("invalid search query: " + searchQuery);
		}

		// TODO revisit, for now non-paged api just uses a large page ;)
		PageRequest pageRequest = new PageRequest(1, 500);
		Map<String, PagedResources<MapItemDto>> pagedSearchResults = this.searchEngine.search(tokenizedSearchQuery,
				pageRequest);
		searchResults = this.convertPagedSearchResultsToNonPagedSearchResults(pagedSearchResults);

		return searchResults;
	}

}
