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
package com.expedia.seiso.domain.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Build;
import com.expedia.seiso.domain.entity.Deployment;
import com.expedia.seiso.domain.entity.Environment;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestResource(path = RepoKeys.DEPLOYMENTS)
public interface DeploymentRepo extends PagingAndSortingRepository<Deployment, Long> {

    @FindByKey
    @Query("from Deployment d where cast(d.id as string) = ?1")
    Deployment findById(String id);

	Page<Deployment> findByBuildAndEnvironment(Build build, Environment environment, Pageable pageable);

    @RestResource(path = "find-by-service-and-environment")
    @Query("from Deployment d where d.build.commit.service.key = :serviceKey and d.environment.key = :environmentKey")
    List<Deployment> findByServiceAndEnvironment(@Param("serviceKey")String serviceKey,@Param("environmentKey")String environmentKey);
}
