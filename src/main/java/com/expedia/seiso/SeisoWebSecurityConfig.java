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

import lombok.val;
import lombok.extern.slf4j.XSlf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

// See http://blog.springsource.org/2013/07/03/spring-security-java-config-preview-web-security/
// http://stackoverflow.com/questions/8658584/spring-security-salt-for-custom-userdetails
// http://stackoverflow.com/questions/8521251/spring-securitypassword-encoding-in-db-and-in-applicationconext
// http://docs.spring.io/spring-security/site/docs/3.2.x/guides/helloworld.html

// Also for OAuth2
// http://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified

/**
 * Java configuration for Seiso security.
 * 
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
//@Configuration
//@EnableWebSecurity // creates the springSecurityFilterChain bean
//@EnableWebMvcSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@XSlf4j
public class SeisoWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	// FIXME Update to @Autowired configureGlobal() per
	// docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle/#jc-authentication
	// See StackOverflow question 20651043 as well.
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		log.info("Creating AuthenticationManager with UserService {}", userDetailsService.getClass());
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	// @Override
	// public void configure(AuthenticationManagerBuilder auth) throws Exception {
	// auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	// }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/**").permitAll()
				.antMatchers(HttpMethod.POST, "/v1/machines/search").permitAll()
				.anyRequest().authenticated()
				.and()
			.httpBasic()
				.authenticationEntryPoint(entryPoint())
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint())
				.and()
//			.headers()
//				.cacheControl()
//				.contentTypeOptions()
//				.frameOptions()
//				.httpStrictTransportSecurity()
//				.and()
			.csrf()
				.disable();
		// @formatter:on
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		log.trace("Creating password encoder");
		return new BCryptPasswordEncoder();
	}

	@Bean
	public BasicAuthenticationEntryPoint entryPoint() {
		val basicAuthEntryPoint = new BasicAuthenticationEntryPoint();
		basicAuthEntryPoint.setRealmName("Seiso");
		return basicAuthEntryPoint;
	}
}
