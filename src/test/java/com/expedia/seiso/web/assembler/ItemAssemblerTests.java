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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;

import com.expedia.seiso.domain.entity.Region;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ProjectionNode;

/*
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemAssemblerTests {

	// Class under test
	@InjectMocks
	private ItemAssembler assembler;

	// Dependencies
	@Mock
	private Repositories repositories;
	@Mock
	private EntityLinks links;
	@Mock
	private ItemMetaLookup itemMetaLookup;

	// Test data
	private Region region;
	@Mock
	private ProjectionNode regionQuery;
	@Mock
	private PersistentEntity regionMeta;
	@Mock
	private PersistentProperty regionKeyProp;
	@Mock
	private LinkBuilder linkBuilder;
	@Mock
	private Link link;
	@Mock
	private ItemMeta itemTypeMeta;

	@Before
	public void init() throws Exception {
		this.assembler = new ItemAssembler();
		MockitoAnnotations.initMocks(this);
		initTestData();
		initDependencies();
	}

	private void initTestData() {
		this.region = new Region().setKey("amazon-us-west").setName("Amazon US West");

		when(regionMeta.getPersistentProperty("key")).thenReturn(regionKeyProp);
		when(linkBuilder.withSelfRel()).thenReturn(link);
		when(itemMetaLookup.getItemMeta((Class<Region>) anyObject())).thenReturn(itemTypeMeta);
	}

	private void initDependencies() {
		when((PersistentEntity) repositories.getPersistentEntity(Region.class)).thenReturn(
				regionMeta);
		when(links.linkForSingleResource(eq(Region.class), anyString())).thenReturn(linkBuilder);
	}

	@Test
	public void toDto() {
		val actualDto = assembler.toDto(region, regionQuery);
		assertNotNull(actualDto);

		// FIXME Want to do this stuff, but need to beef up the regionMeta above. Or else use a real one. [WLW]
		// val actualDtoMap = actualDto.getContent();
		// assertEquals(region.getKey(), actualDtoMap.get("key"));
		// assertEquals(region.getName(), actualDtoMap.get("name"));
	}

	@Test
	public void toDto_nullEntity() {
		val actualDto = assembler.toDto(null, regionQuery);
		assertNull(actualDto);
	}

	@Test(expected = NullPointerException.class)
	public void toDto_nullQuery() {
		assembler.toDto(region, null);
	}

	@Test
	public void toDtoList() {
		val actualDtos = assembler.toDtoList(Collections.singletonList(region), regionQuery);
		assertNotNull(actualDtos);
	}

	@Test
	public void toDtoList_nullEntities() {
		val actualDtos = assembler.toDtoList(null, regionQuery);
		assertNull(actualDtos);
	}

	@Test(expected = NullPointerException.class)
	public void toDtoList_nullQuery() {
		assembler.toDtoList(Collections.singletonList(region), null);
	}

	@Test
	public void toDtoPage() {
		// TODO
	}
}
