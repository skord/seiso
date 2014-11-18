package com.expedia.seiso.domain.repo.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

// TODO revisit string approach, look at replacing with criteria to protect against injection
public class QueryFactory
{
    private String calculateTableAlias( String entityName )
    {
        String tableAlias = entityName.substring( 0, 1 ).toLowerCase();
        
        return tableAlias;
    }
    
    private void appendQueryRoot( StringBuilder stringBuilder, String entityName, String tableAlias )
    {
        stringBuilder.append( "from " )
                     .append( entityName )
                     .append( " " )
                     .append( tableAlias )
                     .append( " where " );
    }
    
    private List<String> appendQueryLikeClausesForField( StringBuilder queryStringBuilder, String tableAlias, String fieldName, Set<String> searchTokens )
    {
        List<String> parameterNames = new ArrayList<String>();
        
        if( queryStringBuilder == null )
        {
            queryStringBuilder = new StringBuilder();
        }
        
        for( int tokenIndex = 0; tokenIndex < searchTokens.size(); tokenIndex++ )
        {
            String parameterName = new StringBuilder( "param" ).append( tokenIndex ).toString();
            parameterNames.add( parameterName );
            queryStringBuilder.append( tableAlias )
                              .append( "." )
                              .append( fieldName )
                              .append( " like :" )
                              .append( parameterName )
                              .append( " " );
            if( tokenIndex < searchTokens.size() - 1 )
            {
                queryStringBuilder.append( "or " );
            }
        }
        
        return parameterNames;
    }
    
    private List<String> appendQueryString( StringBuilder queryStringBuilder, String entityName, Set<String> fieldNames, Set<String> searchTokens )
    {
        List<String> parameterNames = new ArrayList<String>();
                
        String tableAlias = this.calculateTableAlias( entityName );
        this.appendQueryRoot( queryStringBuilder, entityName, tableAlias );

        int fieldNamesIndex = 0;
        for( String fieldName : fieldNames )
        {
            // TODO improve this, technically the parameter names are created every time we call this append method - they should always be consistent but it is wasting resources a bit
            parameterNames.addAll( this.appendQueryLikeClausesForField( queryStringBuilder, tableAlias, fieldName, searchTokens ) );
            if( ++fieldNamesIndex < fieldNames.size() )
            {
                queryStringBuilder.append(  "or " );
            }
        }
    
        return parameterNames;
    }
    
    private void setParameters( Query jpaQuery, List<String> parameterNames , Set<String> searchTokens )
    {
        int parameterIndex = 0;
        for( String searchToken : searchTokens )
        {
            jpaQuery.setParameter( parameterNames.get( parameterIndex++ ), searchToken );
        }
    }
    
    public Query buildQuery( @NotEmpty String entityName, @NotNull EntityManager entityManager, @NotNull Set<String> fieldNames, @NotNull Set<String> searchTokens )
    {
        Query jpaQuery = null;
        
        Assert.notEmpty( fieldNames, "empty field names" );
        Assert.notEmpty( searchTokens, "empty search tokens" );
        
        StringBuilder queryStringBuilder = new StringBuilder(); 
        
        List<String>  parameterNames     = this.appendQueryString( queryStringBuilder, entityName, fieldNames, searchTokens );
        jpaQuery           = entityManager.createQuery( queryStringBuilder.toString() );
        this.setParameters( jpaQuery, parameterNames, searchTokens );

        return jpaQuery;
    }
}
