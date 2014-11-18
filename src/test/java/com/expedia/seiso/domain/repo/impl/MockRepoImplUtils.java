package com.expedia.seiso.domain.repo.impl;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@SuppressWarnings( "rawtypes" )
public class MockRepoImplUtils extends RepoImplUtils
{
    private Page mockSearchResult;
    
    public MockRepoImplUtils( Page mockSearchResult )
    {
        super();
        
        this.mockSearchResult = mockSearchResult;
    }
    
    @SuppressWarnings( "unchecked" )
    public <T> Page<T> search( @NotNull String entityName,
                               @NotNull EntityManager entityManager, 
                               @NonNull Set<String> fieldNames, 
                               @NotNull Set<String> searchTokens, 
                                        Pageable pageable )
    {
        return this.mockSearchResult;
    }
}
