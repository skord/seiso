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

import java.util.List;
import java.util.Map;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.model.BeanWrapper;

import com.expedia.seiso.core.util.AuditUtils;
import com.expedia.seiso.domain.entity.Item;

/**
 * Implemented as a standalone class (rather than as an anonymous inner class) to faciliate unit testing.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RequiredArgsConstructor
@XSlf4j
public class ItemAssociationHandler implements SimpleAssociationHandler {
	@NonNull private final ItemAssembler assembler;
	@NonNull private final ProjectionNode projectionNode;
	@NonNull private final BeanWrapper<Item> wrapper;
	@NonNull private final Map<String, Object> model;
	
	@Override
	@SuppressWarnings("unchecked")
	public void doWithAssociation(Association<? extends PersistentProperty<?>> assoc) {
		
		// Hm, val doesn't work here for some reason.
		PersistentProperty<?> prop = assoc.getInverse();
		
		val propName = prop.getName();
		
		// We handle audit properties elsewhere.
		if (AuditUtils.isAuditProperty(propName)) { return; }
		
		val propType = prop.getType();
		val child = projectionNode.getChild(propName);
		
		if (child != null) {
			if (Item.class.isAssignableFrom(propType)) {
				val propEntity = (Item) wrapper.getProperty(prop);
				val propDto = assembler.toDto(propEntity, child);
				model.put(propName, propDto);
			} else if (List.class.isAssignableFrom(propType)) {
				val propEntityList = (List<? extends Item>) wrapper.getProperty(prop);
				val dtoList = assembler.toDtoList(propEntityList, child);
				model.put(propName, dtoList);
			} else {
				log.warn("Don't know how to handle association type {}", propType);
			}
		}
	}
}
