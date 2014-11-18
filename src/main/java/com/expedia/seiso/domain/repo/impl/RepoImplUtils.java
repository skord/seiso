package com.expedia.seiso.domain.repo.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.repo.search.query.QueryFactory;

/**
 * @author Ken Van Eyk (kvaneyk@expedia.com)
 */
public class RepoImplUtils
{
    private static RepoImplUtils SINGLETON = new RepoImplUtils();
    
    public static RepoImplUtils getInstance()
    {
        return RepoImplUtils.SINGLETON;
    }
    
    protected RepoImplUtils()
    {
    }
    
    // TODO use pageable ?
    @SuppressWarnings( "unchecked" )
    public <T> Page<T> search( @NotNull String entityName,
                               @NotNull EntityManager entityManager, 
                               @NonNull Set<String> fieldNames, 
                               @NotNull Set<String> searchTokens, 
                                        Pageable pageable )
    {
        PageImpl<T> page = null;

        Query   query = new QueryFactory().buildQuery( entityName, entityManager, fieldNames, searchTokens );
        List<T> items = query.getResultList();
                page  = new PageImpl<T>( items );
        
        return page;
    }
}
