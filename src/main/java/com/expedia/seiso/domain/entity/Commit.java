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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.expedia.seiso.core.ann.Projection;
import com.expedia.seiso.core.ann.Projections;
import com.expedia.seiso.domain.entity.key.ItemKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = {"service", "revision"})
@ToString(of = {"service", "revision"})
@Entity
@Projections({
	@Projection(cardinality = Projection.Cardinality.COLLECTION, paths = { "service" }),
	@Projection(cardinality = Projection.Cardinality.SINGLE, paths = { "service" })
})
public class Commit extends AbstractItem {

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "revision")
    private String revision;

	@NonNull
    @OneToMany(mappedBy = "commit")
    private List<Build> builds = new ArrayList<>();

    @Column(name = "date_created")
    private Date dateCreated;

	@Override
	public ItemKey itemKey() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
