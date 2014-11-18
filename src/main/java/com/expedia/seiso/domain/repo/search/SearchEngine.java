package com.expedia.seiso.domain.repo.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.validation.constraints.NotNull;

import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.repo.search.query.token.TokenizedSearchQuery;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.dto.MapItemDto;

@Component
public class SearchEngine<T extends Item, ID extends Serializable>
{
    private class PagedSearchResult<TYPE extends Item>
    {
        private Class<TYPE> resultType;
        private PagedResources<MapItemDto> result;
        
        private PagedSearchResult( Class<TYPE> resultType, PagedResources<MapItemDto> result )
        {
            this.resultType = resultType;
            this.result = result;
        }
    }
    
    private class PagedSearchTask implements Callable<PagedSearchResult<T>>
    {
        private SearchableRepository<T, ID> searchableRepository;
        private ItemAssembler itemAssembler;
        private Set<String>   searchTokens;
        private Pageable      pageable;

        private PagedSearchTask( @NotNull ItemAssembler itemAssembler, @NotNull SearchableRepository<T, ID> searchableRepository, Set<String> searchTokens, Pageable pageable )
        {
            this.itemAssembler = itemAssembler;
            this.searchableRepository = searchableRepository;
            this.searchTokens = searchTokens;
            this.pageable = pageable;
        }
        
        @Override
        public PagedSearchResult<T> call() throws Exception
        {
            PagedSearchResult<T> pagedSearchResult = null;
            
            Page<T> itemPage = searchableRepository.search( this.searchTokens, this.pageable );
            Class<T> resultType = searchableRepository.getResultType();
            PagedResources<MapItemDto> result = this.itemAssembler.toDtoPage( itemPage );
            pagedSearchResult = new PagedSearchResult<T>( resultType, result );
            
            return pagedSearchResult;
        }
        
    }

    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    
    private ItemAssembler   itemAssembler;
    private Repositories    repositories;
    private ExecutorService executorService;


    @Autowired( required = false )
    public SearchEngine( ItemAssembler itemAssembler, Repositories repositories )
    {
        this( itemAssembler, repositories, Executors.newFixedThreadPool( SearchEngine.DEFAULT_THREAD_POOL_SIZE ) );
    }
    
    @Autowired( required = false )
    public SearchEngine( @NotNull ItemAssembler itemAssembler, @NotNull Repositories repositories, @NotNull ExecutorService executorService )
    {
        this.itemAssembler   = itemAssembler;
        this.repositories    = repositories;
        this.executorService = executorService;
    }
    
    @SuppressWarnings( "unchecked" )
    private Collection<PagedSearchTask> createPagedSearchTasks( Set<String> searchTokens, Pageable pageable, Map<String, PagedResources<MapItemDto>> searchResultsByEntityType )
    {
        Collection<PagedSearchTask> pagedSearchTasks = new ArrayList<PagedSearchTask>();
        
        Iterator<Class<?>> entityTypeIterator = this.repositories.iterator();
        while( entityTypeIterator.hasNext() )
        {
            Object repository = repositories.getRepositoryFor( entityTypeIterator.next() );
            if( repository instanceof SearchableRepository )
            {
                pagedSearchTasks.add( new PagedSearchTask( this.itemAssembler, (SearchableRepository<T, ID>)repository, searchTokens, pageable) );
            }   
        }
        
        return pagedSearchTasks;
    }
    
    public Map<String,PagedResources<MapItemDto>> search( TokenizedSearchQuery tokenizedSearch, Pageable pageable ) throws InterruptedException, ExecutionException
    {
        val searchResultsByEntityType = new HashMap<String,PagedResources<MapItemDto>>();

        if( tokenizedSearch != null )
        {
            Set<String> searchTokens = tokenizedSearch.getTokens();
            if( !CollectionUtils.isEmpty( searchTokens ) )
            {
                Collection<PagedSearchTask> pagedSearchTasks = this.createPagedSearchTasks( searchTokens, pageable, searchResultsByEntityType );
                List<Future<PagedSearchResult<T>>> pagedSearchTaskResults = this.executorService.invokeAll( pagedSearchTasks );
                
                for( Future<PagedSearchResult<T>> pagedSearchTaskResult : pagedSearchTaskResults )
                {
                    PagedSearchResult<T> pagedSearchResult = pagedSearchTaskResult.get();
                    searchResultsByEntityType.put( pagedSearchResult.resultType.getSimpleName().toLowerCase(), pagedSearchResult.result );
                }
            }            
        }
        
        return searchResultsByEntityType;
    }
}
