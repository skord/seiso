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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.gateway.ActionGateway;
import com.expedia.seiso.gateway.model.BulkNodeActionRequest;

/**
 * Controller to handle action requests, which go to external services.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION + "/actions")
@XSlf4j
public class ActionController {
	@Autowired private ActionGateway actionGateway;
	
	// FIXME For now, just assume that requests are BulkNodeActionRequests. [WLW]
	@RequestMapping(method = RequestMethod.POST)
	public void post(@RequestBody BulkNodeActionRequest request) {
		log.info("Publishing action request: code={}", request.getCode());
		actionGateway.publish(request);
	}
}
