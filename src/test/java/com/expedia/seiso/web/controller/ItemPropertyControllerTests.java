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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import lombok.val;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.expedia.seiso.core.exception.NotFoundException;
import com.expedia.seiso.domain.entity.Endpoint;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.Person;
import com.expedia.seiso.domain.entity.Service;
import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.dto.MapItemDto;

public class ItemPropertyControllerTests {
	private static final String GOOD_KEY = "good-key";
	private static final String BAD_KEY = "bad-key";

	// Class under test
	@InjectMocks
	private ItemPropertyController controller;

	// Dependencies
	@Mock
	private ItemMetaLookup itemMetaLookup;
	@Mock
	private ItemService itemService;
	@Mock
	private ItemAssembler itemAssembler;

	// Test data
	private Machine machine;
	private Node node;
	private NodeIpAddress ipAddress;
	private Person person;
	private Person manager;
	private Person directReport;
	private Service service;
	private ServiceInstance serviceInstance;
	private ServiceInstancePort port;
	private List<NodeIpAddress> ipAddresses;
	private List<ServiceInstancePort> ports;
	private List<Person> directReports;
	@Mock
	private ItemMeta machineMeta;
	@Mock
	private ItemMeta nodeMeta;
	@Mock
	private ItemMeta nodeIpAddressMeta;
	@Mock
	private ItemMeta personMeta;
	@Mock
	private ItemMeta serviceMeta;
	@Mock
	private ItemMeta serviceInstanceMeta;
	@Mock
	private ItemMeta serviceInstancePortMeta;
	@Mock
	private MapItemDto machineDto;
	@Mock
	private MapItemDto managerDto;
	@Mock
	private MapItemDto serviceDto;
	@Mock
	private List<MapItemDto> ipAddressDtoList;
	@Mock
	private List<MapItemDto> portDtoList;
	@Mock
	private List<MapItemDto> directReportDtoList;

	@Before
	public void setUp() throws Exception {
		this.controller = new ItemPropertyController();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {

		// @formatter:off

		this.machine = new Machine().setName("my-machine");
		this.node = new Node().setName("my-node").setMachine(machine);

		this.service = new Service().setKey("seiso-api").setName("Seiso API");
		this.serviceInstance = new ServiceInstance().setKey("seiso-api-prod").setService(service);

		this.manager = new Person().setUsername("michael");
		this.person = new Person().setUsername("jamal").setManager(manager);
		this.directReport = new Person().setUsername("helen").setManager(person);
		this.directReports = Collections.singletonList(directReport);
		person.setDirectReports(directReports);

		this.ipAddress = new NodeIpAddress().setNode(node);
		this.ipAddresses = Collections.singletonList(ipAddress);
		node.setIpAddresses(ipAddresses);

		this.port = new ServiceInstancePort().setServiceInstance(serviceInstance).setNumber(8080).setProtocol("http");
		this.ports = Collections.singletonList(port);
		serviceInstance.setPorts(ports);

		when(nodeMeta.getPropertyName("machine")).thenReturn("machine");
		when(nodeMeta.getPropertyName("ip-addresses")).thenReturn("ipAddresses");

		when(personMeta.getPropertyName("manager")).thenReturn("manager");
		when(personMeta.getPropertyName("direct-reports")).thenReturn("directReports");

		when(serviceInstanceMeta.getPropertyName("service")).thenReturn("service");
		when(serviceInstanceMeta.getPropertyName("ports")).thenReturn("ports");

		// @formatter:on
	}

	private void initDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.ENDPOINTS)).thenReturn(Endpoint.class);
		when(itemMetaLookup.getItemClass(RepoKeys.NODES)).thenReturn(Node.class);
		when(itemMetaLookup.getItemClass(RepoKeys.PEOPLE)).thenReturn(Person.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICES)).thenReturn(Service.class);
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICE_INSTANCES)).thenReturn(ServiceInstance.class);

		when(itemMetaLookup.getItemMeta(Machine.class)).thenReturn(machineMeta);
		when(itemMetaLookup.getItemMeta(Node.class)).thenReturn(nodeMeta);
		when(itemMetaLookup.getItemMeta(NodeIpAddress.class)).thenReturn(nodeIpAddressMeta);
		when(itemMetaLookup.getItemMeta(Person.class)).thenReturn(personMeta);
		when(itemMetaLookup.getItemMeta(Service.class)).thenReturn(serviceMeta);
		when(itemMetaLookup.getItemMeta(ServiceInstance.class)).thenReturn(serviceInstanceMeta);
		when(itemMetaLookup.getItemMeta(ServiceInstancePort.class)).thenReturn(serviceInstancePortMeta);

		when(itemService.find(new SimpleItemKey(Node.class, GOOD_KEY))).thenReturn(node);
		when(itemService.find(new SimpleItemKey(Node.class, BAD_KEY))).thenThrow(new NotFoundException());
		when(itemService.find(new SimpleItemKey(Person.class, GOOD_KEY))).thenReturn(person);
		when(itemService.find(new SimpleItemKey(Person.class, BAD_KEY))).thenThrow(new NotFoundException());
		when(itemService.find(new SimpleItemKey(ServiceInstance.class, GOOD_KEY))).thenReturn(serviceInstance);
		when(itemService.find(new SimpleItemKey(ServiceInstance.class, BAD_KEY))).thenThrow(new NotFoundException());

		when(itemAssembler.toDto(eq(machine), (ProjectionNode) anyObject())).thenReturn(machineDto);
		when(itemAssembler.toDto(eq(manager), (ProjectionNode) anyObject())).thenReturn(managerDto);
		when(itemAssembler.toDto(eq(service), (ProjectionNode) anyObject())).thenReturn(serviceDto);
		when(itemAssembler.toDtoList(eq(ipAddresses), (ProjectionNode) anyObject())).thenReturn(ipAddressDtoList);
		when(itemAssembler.toDtoList(eq(ports), (ProjectionNode) anyObject())).thenReturn(portDtoList);
		when(itemAssembler.toDtoList(eq(directReports), (ProjectionNode) anyObject())).thenReturn(directReportDtoList);
	}

	// =================================================================================================================
	// Simple properties
	// =================================================================================================================

	@Test
	public void getProperty_node_machine() {
		val result = controller.getProperty(RepoKeys.NODES, GOOD_KEY, "machine");
		assertEquals(machineDto, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_node_machine_notFound() {
		controller.getProperty(RepoKeys.NODES, BAD_KEY, "machine");
	}

	@Test
	public void getProperty_person_manager() {
		val result = controller.getProperty(RepoKeys.PEOPLE, GOOD_KEY, "manager");
		assertEquals(managerDto, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_person_manager_notFound() {
		controller.getProperty(RepoKeys.PEOPLE, BAD_KEY, "manager");
	}

	@Test
	public void getProperty_serviceInstance_service() {
		val result = controller.getProperty(RepoKeys.SERVICE_INSTANCES, GOOD_KEY, "service");
		assertEquals(serviceDto, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_serviceInstance_service_notFound() {
		controller.getProperty(RepoKeys.SERVICE_INSTANCES, BAD_KEY, "service");
	}

	// =================================================================================================================
	// Collection properties
	// =================================================================================================================

	// TODO Need to decide whether we are really supporting this endpoint. It doesn't match the database schema (which
	// might be OK). [WLW]
	@Ignore
	@Test
	public void getProperty_node_endpoints() {
		val result = controller.getProperty(RepoKeys.NODES, GOOD_KEY, "endpoints");
	}

	@Test
	public void getProperty_node_ipAddresses() {
		val result = controller.getProperty(RepoKeys.NODES, GOOD_KEY, "ip-addresses");
		assertEquals(ipAddressDtoList, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_node_ipAddresses_notFound() {
		controller.getProperty(RepoKeys.NODES, BAD_KEY, "ip-addresses");
	}

	@Test
	public void getProperty_person_directReports() {
		val result = controller.getProperty(RepoKeys.PEOPLE, GOOD_KEY, "direct-reports");
		assertEquals(directReportDtoList, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_person_directReports_notFound() {
		controller.getProperty(RepoKeys.PEOPLE, BAD_KEY, "direct-reports");
	}

	@Test
	public void getProperty_serviceInstance_ports() {
		val result = controller.getProperty(RepoKeys.SERVICE_INSTANCES, GOOD_KEY, "ports");
		assertEquals(portDtoList, result);
	}

	@Test(expected = NotFoundException.class)
	public void getProperty_serviceInstance_ports_notFound() {
		controller.getProperty(RepoKeys.NODES, BAD_KEY, "ports");
	}

	// =================================================================================================================
	// Collection element properties
	// =================================================================================================================

}
