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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

// For now we are using the deployment's dateCreated to sequence deployments. Ideally I'd like to use a deployment
// number (dateCreated isn't guaranteed to be unique), but this will need to wait until we have an actual deployment
// service. (Similar to how a Jenkins generates a set of build numbers.) For now we enforce uniqueness using a database
// constraint involving the dateCreated.
// 
// Note that the build's build number is not an appropriate proxy for the deployment number. The reason is that we
// can go back and manually deploy an build generated from an old build. In this case the build number would return
// the wrong build. The deployment number (or dateCreated, as above) avoids this problem. [WLW]

// @Builder isn't adding build() because there's already a build(Build) method. Ugh. [WLW]

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@ToString(of = { "build", "environment", "dateCreated" })
@EqualsAndHashCode(callSuper = true, of = { "build", "environment", "dateCreated" })
@Entity
@Projections({
	@Projection(cardinality = Projection.Cardinality.COLLECTION, paths = { "build", "environment" }),
	@Projection(cardinality = Projection.Cardinality.SINGLE, paths = { "build", "environment" })
})
public class Deployment extends AbstractItem {
	
	@ManyToOne
	@JoinColumn(name = "build_id")
	private Build build;
	
	@ManyToOne
	@JoinColumn(name = "environment_id")
	private Environment environment;
	
	@Column(name = "date_created")
	private Date dateCreated;

	@Override
	public ItemKey itemKey() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
