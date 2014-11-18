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
package com.expedia.seiso.web.dto;

import java.util.Map;

import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Map-backed resource. We use this for outgoing representations.
 * 
 * @see PEItemDto
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@AllArgsConstructor
@JsonPropertyOrder({ "selfUri", "id", "content", "auditData" })
public class MapItemDto {
	private String selfUri;
	private Long id;
	private Map<String, Object> content;
	private AuditData auditData;
	
	@JsonProperty("_self")
	@JsonInclude(Include.NON_NULL)
	public String getSelfUri() { return selfUri; }
	
	@JsonProperty
	public Long getId() { return id; }
	
	@JsonAnyGetter
	public Map<String, Object> getContent() { return content; }
	
	@JsonProperty("audit")
	@JsonInclude(Include.NON_NULL)
	public AuditData getAuditData() { return auditData; }
}
