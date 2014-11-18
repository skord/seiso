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

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.support.Repositories;
import org.springframework.web.context.request.WebRequest;

import com.expedia.seiso.domain.entity.ServiceInstance;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaImpl;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.domain.repo.ServiceInstanceRepo;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.controller.ItemSearchController;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemSearchControllerTests {
	
	// Class under test
	@InjectMocks private ItemSearchController controller;
	
	// Dependencies
	@Mock private ItemMetaLookup itemMetaLookup;
	@Mock private Repositories repositories;
	@Mock private ConversionService conversionService;
	@Mock private ItemAssembler itemAssembler;
	@Mock private ServiceInstanceRepo serviceInstanceRepo;
	
	// Test data
	@Mock private WebRequest request;
	private ItemMeta serviceInstanceMeta;
	
	@Before
	public void init() throws Exception {
		this.controller = new ItemSearchController();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		this.serviceInstanceMeta = new ItemMetaImpl(ServiceInstance.class, ServiceInstanceRepo.class, true);
	}
	
	private void initDependencies() {
		when(itemMetaLookup.getItemClass(RepoKeys.SERVICE_INSTANCES)).thenReturn(ServiceInstance.class);
		when(itemMetaLookup.getItemMeta(ServiceInstance.class)).thenReturn(serviceInstanceMeta);
		when(repositories.getRepositoryFor(ServiceInstance.class)).thenReturn(serviceInstanceRepo);
	}
	
	@Test
	public void search() {
		controller.search(RepoKeys.SERVICE_INSTANCES, "find-by-environment-and-eos-managed", request);
		verify(serviceInstanceRepo).findByEnvironmentKeyAndEosManaged(anyString(), anyBoolean());
	}
}
