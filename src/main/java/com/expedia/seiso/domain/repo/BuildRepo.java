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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.expedia.seiso.core.ann.FindByKey;
import com.expedia.seiso.core.ann.RestResource;
import com.expedia.seiso.domain.entity.Build;
import com.expedia.seiso.domain.entity.Service;

import java.util.List;

/**
 * @author Willie Wheeler (wwheeler@expedia.com)
 */
@RestResource(path = RepoKeys.BUILDS)
public interface BuildRepo extends PagingAndSortingRepository<Build, Long> {

    @FindByKey
    @Query("from Build b where cast(b.id as string) = ?1")
    Build findById(String id);

	@Query("from Build b where b.commit.service = ?1")
	Page<Build> findByService(Service service, Pageable pageable);

    @RestResource(path = "find-by-service")
    @Query("from Build b where b.commit.service.key = :key")
    List<Build> findByServiceKey(@Param("key")String key);

    @RestResource(path = "find-by-build-number")
    List<Build> findByBuildNumber(@Param("buildNumber") String buildNumber);

    @RestResource(path = "find-by-version")
    List<Build> findByVersion(@Param("version") String version);
}
