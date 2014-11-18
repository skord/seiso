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

import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.service.ItemService;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.assembler.ProjectionNode;
import com.expedia.seiso.web.dto.MapItemDto;

public abstract class AbstractItemController {
	@Autowired @Getter private ItemService itemService;
	@Autowired @Getter private ItemAssembler itemAssembler;
	
	protected MapItemDto get(@NonNull ItemKey key) {
		val item = itemService.find(key);
		return itemAssembler.toDto(item);
	}
	
	protected MapItemDto get(@NonNull ItemKey key, @NonNull ProjectionNode projectionNode) {
		val item = itemService.find(key);
		return itemAssembler.toDto(item, projectionNode);
	}
	
	protected void put(@NonNull Item item) {
		itemService.save(item);
	}
	
	protected void delete(@NonNull ItemKey key) {
		itemService.delete(key);
	}
}
