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

import java.util.ArrayList;

import lombok.val;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.Repositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.expedia.seiso.core.config.DataSourceProperties;
import com.expedia.seiso.core.util.ApplicationContextProvider;
import com.expedia.seiso.domain.repo.RepoPackageMarker;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties
//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableJpaRepositories(basePackageClasses = { RepoPackageMarker.class })
@EnableScheduling
@EnableTransactionManagement
public class Seiso {
    
    public static final String VERSION = "v1";
    
	@Autowired private ListableBeanFactory beanFactory;
	@Autowired private DataSourceProperties dataSourceSettings;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Seiso.class, args);
	}
	
	// Registered automatically
//	@Configuration
//	@Profile("default")
//	@PropertySource("classpath:/spring/seiso-api-environment.properties")
//	static class Defaults { }
	
	// Registered automatically
//	@Configuration
//	@Profile("continuous-integration")
//	@PropertySource("classpath:/spring/seiso-api-environment-ci.properties")
//	static class ContinuousIntegration { }
	
	@Bean
	public SeisoRabbitConfig seisoIntegrationConfig() {
		return new SeisoRabbitConfig();
	}
	
	@Bean
	public SeisoWebConfig seisoWebConfig() {
		return new SeisoWebConfig();
	}
	
	@Bean
	public SeisoWebSecurityConfig seisoWebSecurityConfig() {
		return new SeisoWebSecurityConfig();
	}
	
	@Bean
	public ApplicationContextProvider applicationContextProvider() {
		return new ApplicationContextProvider();
	}
	
	// FIXME DriverManagerDataSource isn't a proper JDBC connection pool. Indeed it is not a pool at all. OK for
	// development but not for production. Use HikariCP, Tomcat-JDBC, C3P0, etc. instead. Also Apache Commons DBCP
	// appears to be out of favor nowadays. [WLW]
	// http://stackoverflow.com/questions/24655247/any-reason-why-spring-hibernate-is-taking-more-time
	// https://github.com/spring-projects/spring-boot/issues/418
	@Bean
	public HikariDataSource dataSource() {
		
		// FIXME Remove hardcodes
//		val hikariConfig = new HikariConfig();
//		hikariConfig.setMaximumPoolSize(10);
//		hikariConfig.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
//		hikariConfig.addDataSourceProperty("serverName", "localhost");
//		hikariConfig.addDataSourceProperty("port", 3306);
//		hikariConfig.addDataSourceProperty("databaseName", "seiso");
//		hikariConfig.addDataSourceProperty("user", env.getProperty(SEISO_DB_USERNAME_KEY));
//		hikariConfig.addDataSourceProperty("password", env.getProperty(SEISO_DB_PASSWORD_KEY));
//		return new HikariDataSource(hikariConfig);
		
		// FIXME Legacy configuration.
		// See https://github.com/brettwooldridge/HikariCP to upgrade.
		val dataSource = new HikariDataSource();
		dataSource.setDriverClassName(dataSourceSettings.getDriverClassName());
		dataSource.setJdbcUrl(dataSourceSettings.getUrl());
		dataSource.setUsername(dataSourceSettings.getUsername());
		dataSource.setPassword(dataSourceSettings.getPassword());
		
		// TODO Add other Hikari data source options.
		
		return dataSource;
	}
	
	@Bean
	public Repositories repositories() { return new Repositories(beanFactory); }
	
	@Bean
	@SuppressWarnings("rawtypes")
	public PersistentEntities persistentEntities() {
		val contexts = new ArrayList<MappingContext<?, ?>>();
		val beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, MappingContext.class);
		for (val context : beans.values()) {
			contexts.add(context);
		}
		return new PersistentEntities(contexts);
	}
}
