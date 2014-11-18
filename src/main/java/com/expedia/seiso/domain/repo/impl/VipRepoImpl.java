package com.expedia.seiso.domain.repo.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.expedia.seiso.domain.entity.Vip;
import com.expedia.seiso.domain.repo.custom.VipRepoCustom;

/**
 * @author Ken Van Eyk (kvaneyk@expedia.com)
 */
public class VipRepoImpl implements VipRepoCustom
{
    private static final String      ENTITY_NAME = "Vip";
    private static final Set<String> FIELD_NAMES = new LinkedHashSet<String>( Arrays.asList( new String[]{ "name" } ) ); 
    
    @PersistenceContext 
    private EntityManager entityManager;
    private RepoImplUtils repoUtils;

    // TODO @PersistenceContext can't be applied at the constructor level, reconcile for consistency https://jira.spring.io/browse/SPR-10443
    public VipRepoImpl()
    {
        this( null );
    }    
    
    public VipRepoImpl( @NotNull EntityManager entityManager )
    {
        this( entityManager, null );
    }
    
    public VipRepoImpl( @NotNull EntityManager entityManager, RepoImplUtils repoUtils )
    {
        this.entityManager = entityManager;
        this.repoUtils = ( repoUtils == null ? RepoImplUtils.getInstance() : repoUtils );
    }
    
    @Override
    public Class<Vip> getResultType()
    {
        return Vip.class;
    }

    @Override
    public Page<Vip> search( @NonNull Set<String> searchTokens, Pageable pageable )
    {
        Page<Vip> searchPage = this.repoUtils.search( VipRepoImpl.ENTITY_NAME, this.entityManager, VipRepoImpl.FIELD_NAMES, searchTokens, pageable );;
        
        return searchPage;
    }
}


