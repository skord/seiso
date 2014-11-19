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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.val;

import org.springframework.http.MediaType;

import com.expedia.seiso.core.util.C;
import com.expedia.seiso.domain.service.response.ErrorObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
public class XUserAgentFilter implements Filter {
	private static final String X_USER_AGENT_HEADER_NAME = "X-User-Agent";

	private static final ErrorObject INVALID_REQUEST_ERROR = new ErrorObject(C.EC_INVALID_REQUEST,
			"HTTP request header '" + X_USER_AGENT_HEADER_NAME + "' is required. "
					+ "Please set it to your application name so we know who to contact when issues arise.");

	private String invalidRequestJson;

	public XUserAgentFilter() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			this.invalidRequestJson = objectMapper.writeValueAsString(INVALID_REQUEST_ERROR);
		} catch (JsonProcessingException e) {
			// Shouldn't happen.
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		val httpRequest = (HttpServletRequest) request;
		val httpResponse = (HttpServletResponse) response;

		val xUserAgent = httpRequest.getHeader(X_USER_AGENT_HEADER_NAME);

		if (xUserAgent == null) {
			httpResponse.setStatus(422);
			httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpResponse.getWriter().println(invalidRequestJson);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}
}
