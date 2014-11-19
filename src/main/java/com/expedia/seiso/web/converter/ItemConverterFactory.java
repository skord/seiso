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
package com.expedia.seiso.web.converter;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;
import com.expedia.seiso.domain.service.ItemService;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Component
public class ItemConverterFactory implements ConverterFactory<String, Item> {
	@Autowired
	private ItemService itemService;

	@Override
	public <T extends Item> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToItemConverter<T>(targetType);
	}

	private final class StringToItemConverter<T extends Item> implements Converter<String, T> {
		private Class<T> itemClass;

		public StringToItemConverter(Class<T> itemClass) {
			this.itemClass = itemClass;
		}

		@Override
		@SuppressWarnings("unchecked")
		public T convert(@NonNull String key) {
			return (T) itemService.find(new SimpleItemKey(itemClass, key));
		}
	}
}
