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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Abstract base class for implementing configuration items.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Data
@Accessors(chain = true)
@ToString(exclude = { "createdBy", "createdDate", "updatedBy", "updatedDate" })
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractItem implements Item {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	// Would like to place the audit data in a component (@Embeddable), but can't do that right now since we're
	// referencing entities, and I think we want to keep the foreign keys to users so we can pull up individual
	// change histories and such. And I don't think we want a separate Audit entity over this. Not sure yet. [WLW]
	
	// Hm, do we really want to @JsonIgnore these? I don't think so, since presumably we want to be able to show
	// audit info in the UI, consume it when generating reports concerning stale data, etc. [WLW]
	
	@CreatedBy
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private User createdBy;
	
	// See http://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html#jpa.auditing
	// See http://blog.countableset.ch/2014/03/08/auditing-spring-data-jpa-java-config/ [WLW]
	@CreatedDate
	@JsonIgnore
	private Date createdDate;
	
	@LastModifiedBy
	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private User updatedBy;
	
	@LastModifiedDate
	@JsonIgnore
	private Date updatedDate;
}
