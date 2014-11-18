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
package com.expedia.seiso.web.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.expedia.seiso.web.filter.XUserAgentFilter;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class XUserAgentFilterTests {
	
	// Class under test
	private XUserAgentFilter filter;
	
	// Test data
	private MockFilterConfig filterConfig;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	@Mock private FilterChain filterChain;
	
	@Before
	public void setUp() throws Exception {
		this.filter = new XUserAgentFilter();
		MockitoAnnotations.initMocks(this);
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void init() throws Exception {
		filter.init(filterConfig);
	}
	
	@Test
	public void doFilter() throws Exception {
		request.addHeader("X-User-Agent", "Ruthless Villain");
		filter.doFilter(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
	}
	
	@Test
	public void doFilterMissingHeader() throws Exception {
		filter.doFilter(request, response, filterChain);
		assertEquals(422, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
	}
	
	@Test
	public void destroy() {
		filter.destroy();
	}
}
