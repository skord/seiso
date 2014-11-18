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
package com.expedia.seiso.web.assembler;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ItemAssociationHandler;
import com.expedia.seiso.web.assembler.ProjectionNode;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemAssociationHandlerTests {
	
	// Class under test
	private ItemAssociationHandler handler;
	
	// Dependencies
	@Mock private ItemAssembler assembler;
	
	// Test data
	@Mock private ProjectionNode query;
	@Mock private BeanWrapper<Item> wrapper;
	@Mock private Map<String, Object> model;
	@Mock private Association assoc;
	@Mock private PersistentProperty<?> prop;
	
	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new ItemAssociationHandler(assembler, query, wrapper, model);
		initTestData();
	}
	
	private void initTestData() {
		when(assoc.getInverse()).thenReturn(prop);
	}
	
	@Test(expected = NullPointerException.class)
	public void requireAssembler() {
		new ItemAssociationHandler(null, query, wrapper, model);
	}
	
	@Test(expected = NullPointerException.class)
	public void requireQuery() {
		new ItemAssociationHandler(assembler, null, wrapper, model);
	}
	
	@Test(expected = NullPointerException.class)
	public void requireWrapper() {
		new ItemAssociationHandler(assembler, query, null, model);
	}
	
	@Test(expected = NullPointerException.class)
	public void requireModel() {
		new ItemAssociationHandler(assembler, query, wrapper, null);
	}
	
//	@Test
//	public void doWithAssociation() {
//		handler.doWithAssociation(assoc);
//	}
}
