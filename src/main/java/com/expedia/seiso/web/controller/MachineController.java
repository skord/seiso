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

import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.domain.repo.MachineRepo;
import com.expedia.seiso.domain.repo.RepoKeys;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION + "/" + RepoKeys.MACHINES)
@XSlf4j
public class MachineController {
	private static final String SEARCH_URI_TEMPLATE = "search";
	
	@Autowired private MachineRepo machineRepo;
	
	
	// TODO class still needed?
	
//	@RequestMapping(
//			value = SEARCH_URI_TEMPLATE,
//			method = RequestMethod.POST,
//			produces = MediaType.APPLICATION_JSON_VALUE,
//			consumes = MediaType.APPLICATION_JSON_VALUE)
//	public Page<Machine> search(
//			@RequestBody @Valid MachineSearch search,
//			BindingResult bindingResult,
//			@PageableDefault(page = C.DEFAULT_PAGE_NUMBER, size = C.DEFAULT_PAGE_SIZE) Pageable pageable) {
//		
//		log.trace("Searching: search={}, pageable={}", search, pageable);
//		
//		if (bindingResult.hasErrors()) {
//			throw new InvalidRequestException("Invalid machine search", bindingResult);
//		}
//		
//		return machineRepo.search(search, pageable);
//	}
}
