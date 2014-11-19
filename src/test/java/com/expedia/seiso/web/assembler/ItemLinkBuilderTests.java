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
import static org.junit.Assert.assertSame;

import java.net.URI;

import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.entity.InfrastructureProvider;
import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.domain.meta.ItemMetaImpl;
import com.expedia.seiso.domain.repo.InfrastructureProviderRepo;
import com.expedia.seiso.web.assembler.ItemLinkBuilder;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class ItemLinkBuilderTests {

	// Class under test
	private ItemLinkBuilder linkBuilder;

	// Test data
	private ItemMeta providerRepoMeta;
	private URI baseUri;
	private ServletRequestAttributes requestAttributes;

	@Before
	public void init() throws Exception {
		this.providerRepoMeta = new ItemMetaImpl(InfrastructureProvider.class, InfrastructureProviderRepo.class, false);
		this.baseUri = new URI("http://www.example.com/");
		this.linkBuilder = new ItemLinkBuilder(baseUri, providerRepoMeta);
		this.requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
	}

	@Test
	public void createNewInstance() {
		val uri = UriComponentsBuilder.fromUri(baseUri);
		val result = linkBuilder.createNewInstance(uri);
		assertNotNull(result);
	}

	@Test
	public void createNewInstance_nullBaseUri() {
		try {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			new ItemLinkBuilder(null, providerRepoMeta);
			// TODO Verification
		} finally {
			RequestContextHolder.resetRequestAttributes();
		}
	}

	@Test
	public void getThis() {
		assertSame(linkBuilder, linkBuilder.getThis());
	}
}
