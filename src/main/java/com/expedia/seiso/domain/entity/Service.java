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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.Projection.Cardinality;
import com.expedia.seiso.domain.entity.key.ItemKey;
import com.expedia.seiso.domain.entity.key.SimpleItemKey;

/**
 * <p>
 * A service.
 * </p>
 * <p>
 * By way of illustration, ExpWeb is a service. The separate points of sale based on different VIPs don't amount to
 * different services.
 * </p>
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "key")
@ToString(callSuper = true, of = { "key", "name", "type" })
@Entity
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = {
			"group",
			"type",
			"owner",
			"createdBy",
			"updatedBy"
	}),
	@Projection(cardinality = Cardinality.SINGLE, paths = {
			"group",
			"type",
			"owner",
			"createdBy",
			"updatedBy"
	}),
	@Projection(cardinality = Cardinality.SINGLE, name = "instances", paths = {
			"serviceInstances.environment",
			"serviceInstances.dataCenter.region.infrastructureProvider",
			"serviceInstances.nodes",
			"createdBy",
			"updatedBy"
	}),
	@Projection(cardinality = Cardinality.SINGLE, name="dependencies", paths = {
			"dependencies.source.type",
			"dependencies.target.type",
			"dependencies.type",
			"dependents.source.type",
			"dependents.target.type",
			"dependents.type",
			"createdBy",
			"updatedBy"
	})
})
public class Service extends AbstractItem {
	@Key @Column(name = "ukey") private String key;
	private String name;
	private String description;
	private String platform;
	private String scmRepository;
	
	@ManyToOne
	@JoinColumn(name = "group_id")
	@Fetch(FetchMode.JOIN)
	private ServiceGroup group;
	
	@ManyToOne
	@JoinColumn(name = "type_id")
	@Fetch(FetchMode.JOIN)
	private ServiceType type;
    
	@ManyToOne
	@JoinColumn(name = "owner_id")
	@Fetch(FetchMode.JOIN)
	private Person owner;
	
	@NonNull
	@OrderBy("name")
	@OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IpAddressRole> ipAddressRoles = new ArrayList<>();
	
	@NonNull
	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("key")
	private List<ServiceInstance> serviceInstances = new ArrayList<>();
	
	// FIXME This should be a query, not a property. We would expect to be able to page these, and we can't do that with
	// properties.
//	@NonNull
//	@OneToMany(mappedBy = "service")
//	private List<Commit> commits = new ArrayList<>();
	
	// The @OrderBys below don't work, because source and target aren't @Embedded. [WLW]
	
	@NonNull
	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("target.name")
	private List<ServiceDependency> dependencies = new ArrayList<>();
	
	@NonNull
	@OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
//	@OrderBy("source.name")
	private List<ServiceDependency> dependents = new ArrayList<>();
	
	@Override
	public ItemKey itemKey() { return new SimpleItemKey(Service.class, key); }
}
