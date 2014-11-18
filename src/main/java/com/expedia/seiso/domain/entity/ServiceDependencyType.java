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
package com.expedia.seiso.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { })
})
public class ServiceDependencyType extends AbstractItem {
	
	@Key
	@Column(name = "ukey")
	private String key;
	
	@Column(name = "name")
	private String name;
	
	@Override
	public ItemKey itemKey() { return new SimpleItemKey(ServiceDependencyType.class, key); }
}
