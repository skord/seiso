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

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.key.NodeIpAddressKey;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.NodeRepo;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.dto.MapItemDto;
import com.expedia.seiso.web.dto.PEItemDto;

// TODO For now, putting @Transactional here because item assembly requires it. If we want to move it to ItemAssembler
// (and I think we do) then we will want to have an ItemAssembler interface and implementation class. [WLW]

@RestController
@RequestMapping(Controllers.REQUEST_MAPPING_VERSION)
@Transactional
public class NodeIpAddressController extends AbstractItemController {
	private static final String NODE_IP_ADDRESS_URI_TEMPLATE = "/nodes/{nodeName}/ip-addresses/{ipAddress}";

	@Autowired
	private ItemMetaLookup itemMetaLookup;
	@Autowired
	private NodeRepo nodeRepo;

	private ProjectionNode defaultSingleProjection;

	@PostConstruct
	public void postConstruct() {
		val itemMeta = itemMetaLookup.getItemMeta(NodeIpAddress.class);
		this.defaultSingleProjection = itemMeta.getProjectionNode(Cardinality.SINGLE, Projection.DEFAULT);
	}

	@RequestMapping(value = NODE_IP_ADDRESS_URI_TEMPLATE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public MapItemDto get(@PathVariable String nodeName, @PathVariable String ipAddress) {
		return super.get(new NodeIpAddressKey(nodeName, ipAddress), defaultSingleProjection);
	}

	@RequestMapping(value = NODE_IP_ADDRESS_URI_TEMPLATE, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void put(@PathVariable String nodeName, @PathVariable String ipAddress, PEItemDto peNipDto) {

		val node = nodeRepo.findByName(nodeName);
		val serviceInstance = node.getServiceInstance();

		// Enrich the node IP address so we can save it. [WLW]
		val nipData = (NodeIpAddress) peNipDto.getItem();
		nipData.setNode(node);
		nipData.setIpAddress(ipAddress);
		nipData.getIpAddressRole().setServiceInstance(serviceInstance);
		super.put(nipData);
	}

	@RequestMapping(value = NODE_IP_ADDRESS_URI_TEMPLATE, method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String nodeName, @PathVariable String ipAddress) {
		super.delete(new NodeIpAddressKey(nodeName, ipAddress));
	}
}
