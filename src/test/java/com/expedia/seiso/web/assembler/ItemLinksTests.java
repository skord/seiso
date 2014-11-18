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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URI;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.support.Repositories;

import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaImpl;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.MachineRepo;
import com.expedia.seiso.web.assembler.ItemLinks;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemLinksTests {
	
	// Class under test
	@InjectMocks private ItemLinks links;
	
	// Dependencies
	@Mock private Repositories repositories;
	@Mock private ItemMetaLookup itemMetaLookup;
	
	// Test data
	private ItemMeta machineRepoMeta;
	
	@Before
	public void init() throws Exception {
		this.links = new ItemLinks(new URI("http://my-base-uri/"));
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}
	
	private void initTestData() {
		this.machineRepoMeta = new ItemMetaImpl(Machine.class, MachineRepo.class, true);
	}
	
	private void initDependencies() {
		when(repositories.hasRepositoryFor(Machine.class)).thenReturn(true);
		when(itemMetaLookup.getItemMeta(Machine.class)).thenReturn(machineRepoMeta);
	}
	
	@Test
	public void supports() {
		val result = links.supports(Machine.class);
		assertTrue(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void supports_nullType() {
		links.supports(null);
	}
	
	@Test
	public void linkFor() {
		val result = links.linkFor(Machine.class);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void linkFor_nullType() {
		links.linkFor(null);
	}
	
	@Test
	public void linkForWithParams() {
		// TODO Not sure what the params are for here. Need to look it up. Assume it's for the ID/key.
		val result = links.linkFor(Machine.class, "my-machine");
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void linkForWithParams_nullType() {
		links.linkFor(null, "my-machine");
	}
	
	@Test
	public void linkToCollectionResource() {
		val result = links.linkToCollectionResource(Machine.class);
		assertNotNull(result);
	}
	
	@Test(expected = NullPointerException.class)
	public void linkToCollectionResource_nullType() {
		links.linkToCollectionResource(null);
	}
	
	@Test
	public void linkToSingleResource() {
		val result = links.linkToSingleResource(Machine.class, "some-key");
		assertNotNull(result);
		val rel = result.getRel();
		assertEquals("machines-single", rel);
	}
	
	@Test(expected = NullPointerException.class)
	public void linkToSingleResource_nullType() {
		links.linkToSingleResource(null, "some-key");
	}
	
	@Test(expected = NullPointerException.class)
	public void linkToSingleResource_nullKey() {
		links.linkToSingleResource(Machine.class, null);
	}
}
