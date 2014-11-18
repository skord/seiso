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
package com.expedia.seiso.domain.repo.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.entity.Vip;


/**
 * @author Ken Van Eyk (kvaneyk@expedia.com)
 */
public class VipRepoImplUnitTest
{
          private Set<String>       searchTokens;
          private Pageable          pageable;
    @Mock private EntityManager     entityManager;
          private MockRepoImplUtils mockRepoImplUtils;
          private Page<Vip>         mockResultsPage;
          private List<Vip>         mockResultList;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks( this );
        
        this.searchTokens = new HashSet<String>();
        searchTokens.add( "foo" );
        this.pageable = new PageRequest( 1, 1 );
        
        this.mockResultList = new ArrayList<Vip>();
        this.mockResultsPage = new PageImpl<Vip>( this.mockResultList );
        this.mockRepoImplUtils = new MockRepoImplUtils( this.mockResultsPage );
    }
    
    @Test
    public void searchTest()
    {
        VipRepoImpl vipRepoImpl = new VipRepoImpl( this.entityManager, this.mockRepoImplUtils );

        Page<Vip> expected = this.mockResultsPage; 
        Page<Vip>   actual = vipRepoImpl.search( this.searchTokens, pageable );
        
        Assert.assertEquals( expected, actual );
    }
    
}
