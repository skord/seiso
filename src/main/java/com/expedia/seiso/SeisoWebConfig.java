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
package com.expedia.seiso;

import java.net.URI;
import java.util.List;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.expedia.seiso.core.config.CustomProperties;
import com.expedia.seiso.domain.entity.IpAddressRole;
import com.expedia.seiso.domain.entity.NodeIpAddress;
import com.expedia.seiso.domain.entity.ServiceInstancePort;
import com.expedia.seiso.domain.repo.RepoKeys;
import com.expedia.seiso.web.assembler.ItemLinks;
import com.expedia.seiso.web.assembler.PageLinks;
import com.expedia.seiso.web.converter.ItemConverterFactory;
import com.expedia.seiso.web.converter.RepoConverter;
import com.expedia.seiso.web.resolver.PEItemDtoListResolver;
import com.expedia.seiso.web.resolver.PEItemDtoResolver;
import com.expedia.seiso.web.resolver.SimplePropertyEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// Don't use @EnableWebMvc here since we are using WebMvcConfigurationSupport directly. [WLW]

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@Configuration
public class SeisoWebConfig extends WebMvcConfigurationSupport {
	
	@Autowired
	private CustomProperties customSettings;
	
	@Autowired
	private RepoConverter repoConverter;
	
	@Autowired
	private ItemConverterFactory itemConverterFactory;
	
	@Autowired
	private PEItemDtoResolver persistentEntityItemDtoResolver;
	
	@Autowired
	private PEItemDtoListResolver persistentEntityItemDtoListResolver;

	// =================================================================================================================
	// Configuration
	// =================================================================================================================

	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(persistentEntityItemDtoResolver);
		argumentResolvers.add(persistentEntityItemDtoListResolver);
		argumentResolvers.add(pageableResolver());
	}

	@Override
	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverter(repoConverter);

		// TODO I don't think we're even using this anymore. [WLW]
		// registry.addConverterFactory(itemConverterFactory);
	}

	@Override
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// @formatter:off
		configurer.favorPathExtension(false).favorParameter(false).ignoreAcceptHeader(false).useJaf(false)
				.defaultContentType(MediaType.APPLICATION_JSON);
		// @formatter:on
	}

	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new ByteArrayHttpMessageConverter());

		val stringConverter = new StringHttpMessageConverter();
		stringConverter.setWriteAcceptCharset(false);
		converters.add(stringConverter);

		val jsonConverter = new MappingJackson2HttpMessageConverter();
		jsonConverter.setObjectMapper(objectMapper());
		converters.add(jsonConverter);
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	@Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		// Lifted from org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration. [WLW]
		registry.addViewController("/").setViewName("forward:/index.html");
	}
	
	
	// =================================================================================================================
	// Beans
	// =================================================================================================================

	// Suppress suffix pattern matching since machine names can have periods. [WLW]
	@Bean
	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		val mapping = new RequestMappingHandlerMapping();
		mapping.setOrder(0);
		mapping.setInterceptors(getInterceptors());
		mapping.setContentNegotiationManager(mvcContentNegotiationManager());
		mapping.setUseSuffixPatternMatch(false);
		return mapping;
	}

	@Bean
	public PageableHandlerMethodArgumentResolver pageableResolver() {
		val resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setMaxPageSize(500);
		resolver.setOneIndexedParameters(false);
		return resolver;
	}

	// TODO Can I inject a multikey map instead of creating a bunch of SimplePropertyEntry beans?
	// Maybe org.springframework.beans.factory.config.MapFactoryBean?
	@Bean
	public SimplePropertyEntry ipAddressRoleEntry() {
		return new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ip-address-roles", IpAddressRole.class);
	}

	@Bean
	public SimplePropertyEntry nodeIpAddressEntry() {
		return new SimplePropertyEntry(RepoKeys.NODES, "ip-addresses", NodeIpAddress.class);
	}

	@Bean
	public SimplePropertyEntry serviceInstancePortEntry() {
		return new SimplePropertyEntry(RepoKeys.SERVICE_INSTANCES, "ports", ServiceInstancePort.class);
	}

	// ObjectMapper is an in-memory tree model parser.
	@Bean
	public ObjectMapper objectMapper() {
		val objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}

	@Bean
	public ItemLinks itemLinks() throws Exception {
		return new ItemLinks(getBaseUri());
	}

	@Bean
	public PageLinks pageLinks() throws Exception {
		return new PageLinks(getBaseUri());
	}

	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		// Need this so we can forward to index.html.
		return new InternalResourceViewResolver();
	}
	
	
	// =================================================================================================================
	// Private
	// =================================================================================================================

	private URI getBaseUri() throws Exception {
		return new URI(slashifyUri(customSettings.getApiBaseUri()));
	}

	private String slashifyUri(String uri) {
		return uri.endsWith("/") ? uri : uri + "/";
	}
}
