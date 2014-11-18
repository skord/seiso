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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.expedia.seiso.core.ann.Key;
import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.core.ann.Projection.Cardinality;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Dave Drinkle (ddrinkle@expedia.com)
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = { "name", "ipAddress", "port" })
@ToString(of = { "name", "ipAddress", "port" })

// http://stackoverflow.com/questions/979200/hibernate-polymorphism
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vip_type", discriminatorType = DiscriminatorType.STRING)
@Projections({
	@Projection(cardinality = Cardinality.COLLECTION, paths = { "loadBalancer" }),
	@Projection(cardinality = Cardinality.SINGLE, paths = { "loadBalancer" })
})
public abstract class Vip extends AbstractItem {
	
	// FIXME Subclasses aren't inheriting @Key. Need to fix the annotation processor.
	@Key
	@Column(name = "name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "load_balancer_id")
	private LoadBalancer loadBalancer;
	
	// FIXME This is the same thing as vip_type, except we're using different keys. Get rid of it. [WLW]
	@Column(name = "type")
	private String type;

	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "port")
	private Integer port;
	
	@Column(name = "service_type")
	private String serviceType;
	
	@Column(name = "load_balancing_method")
	private String loadBalancingMethod;
	
	@Column(name = "comment")
	private String comment;
}
