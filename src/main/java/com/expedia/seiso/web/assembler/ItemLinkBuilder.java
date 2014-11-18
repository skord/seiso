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

import java.net.URI;

import lombok.val;

import org.springframework.hateoas.core.LinkBuilderSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.expedia.seiso.domain.meta.ItemMeta;
import com.expedia.seiso.web.controller.ItemController;

/**
 * {@link ItemLinks} uses this to build links.
 */
// Based on SDR org.springframework.data.rest.webmvc.support.RepositoryLinkBuilder
public class ItemLinkBuilder extends LinkBuilderSupport<ItemLinkBuilder> {
	private ItemMeta itemMeta;
	
	private static UriComponentsBuilder buildBuilder(URI baseUri, ItemMeta itemMeta) {
		val builder = (baseUri == null ?
				ControllerLinkBuilder.linkTo(ItemController.class).toUriComponentsBuilder() :
				UriComponentsBuilder.fromUri(baseUri));
		return builder.path(itemMeta.getRepoKey());
	}
	
	public ItemLinkBuilder(URI baseUri, ItemMeta itemMeta) {
		this(buildBuilder(baseUri, itemMeta), itemMeta);
	}
	
	private ItemLinkBuilder(UriComponentsBuilder builder, ItemMeta itemMeta) {
		super(builder);
		this.itemMeta = itemMeta;
	}
	
	@Override
	protected ItemLinkBuilder createNewInstance(UriComponentsBuilder builder) {
		return new ItemLinkBuilder(builder, itemMeta);
	}
	
	// WTF?
	@Override
	protected ItemLinkBuilder getThis() { return this; }

}
