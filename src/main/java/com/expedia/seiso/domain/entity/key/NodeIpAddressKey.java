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
package com.expedia.seiso.domain.entity.key;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.expedia.seiso.domain.entity.NodeIpAddress;

@Data
@RequiredArgsConstructor
public class NodeIpAddressKey implements ItemKey {
	
	// TODO Decide whether we want to do the lookup by IP address, or by role name. I think it's probably the latter.
	// [WLW]
	@NonNull private String nodeName;
	@NonNull private String ipAddress;
	
	@Override
	public Class<?> getItemClass() { return NodeIpAddress.class; }
}
