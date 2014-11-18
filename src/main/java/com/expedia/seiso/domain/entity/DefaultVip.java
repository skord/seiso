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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.domain.entity.key.ItemKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("default")

// TODO Question: Should we inherit query definitions from the superclass, and then merge the subclass query defs? For
// instance, we define loadBalancer in both places.
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "loadBalancer" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "loadBalancer", "sources" })
})
public class DefaultVip extends Vip {
	
	@NonNull
	@ManyToMany
	@JoinTable(
		name = "content_switching_vip_default_vip",
		joinColumns = @JoinColumn(name = "default_vip_id"),
		inverseJoinColumns = @JoinColumn(name = "content_switching_vip_id"))
	private List<ContentSwitchingVip> sources = new ArrayList<>();

	@Override
	public ItemKey itemKey() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
