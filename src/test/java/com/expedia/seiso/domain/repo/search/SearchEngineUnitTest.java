package com.expedia.seiso.domain.repo.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.PagedResources;

import com.expedia.seiso.domain.entity.Item;
import com.expedia.seiso.domain.entity.Machine;
import com.expedia.seiso.domain.entity.Node;
import com.expedia.seiso.domain.meta.ItemMetaLookup;
import com.expedia.seiso.domain.repo.search.query.token.SpaceDelimitedDatabaseWildCardTokenizer;
import com.expedia.seiso.domain.repo.search.query.token.TokenizedSearchQuery;
import com.expedia.seiso.domain.repo.search.query.token.Tokenizer;
import com.expedia.seiso.web.assembler.ItemAssembler;
import com.expedia.seiso.web.dto.MapItemDto;

public class SearchEngineUnitTest
{
    private class ExpectedSearchResultsByType
    {
        String searchResultsType;
        PagedResources<MapItemDto> searchResults;  
        
        public ExpectedSearchResultsByType( String searchResultsByType, PagedResources<MapItemDto> searchResults )
        {
            this.searchResultsType = searchResultsByType;
            this.searchResults = searchResults;
        }      
    }
    
    private TokenizedSearchQuery   badSearchQuery;
    private TokenizedSearchQuery   goodSearchQuery;
    
    private Pageable pageable = new PageRequest( 1, 1 );
    
    @Mock private Page<Node>  nodeSearchPage;
    @Mock private Page<Machine> machineSearchPage;
    
    @Mock private PagedResources<MapItemDto> nodePagedResource;
    @Mock private PagedResources<MapItemDto> machinePagedResource;
    
    @Mock private ItemMetaLookup itemMetaLookup;
    @Mock private ItemAssembler  itemAssembler;
    @Mock private Repositories   repositories;
    @Mock private Iterator repositoryIterator;
    @Mock private SearchableRepository<Node, String> searchableNodeRepository;
    @Mock private SearchableRepository<Machine, String> searchableMachineRepository;
    
    private SearchEngine<? extends Item, ? extends Serializable> searchEngine;

    @Before
    public void init() throws Exception
    {
        MockitoAnnotations.initMocks( this );
        this.initTestData();
        this.initDependencies();
        this.searchEngine = new SearchEngine<Item, String>( this.itemAssembler, this.repositories );
    }
    
    
    private TokenizedSearchQuery newTokenizedSearchQuery( String query )
    {
        TokenizedSearchQuery tokenizedSearchQuery = null;
        
        Tokenizer tokenizer  = new SpaceDelimitedDatabaseWildCardTokenizer();
        Set<String> tokens   = tokenizer.tokenize( query );
        tokenizedSearchQuery = new TokenizedSearchQuery( query, tokens );
        
        return tokenizedSearchQuery;
    }
    
    private void initTestData()
    {
        this.badSearchQuery  = this.newTokenizedSearchQuery( "bad search query" );
        this.goodSearchQuery = this.newTokenizedSearchQuery( "good search query" );
    }
    
    private <T extends Item> void configureMockSearchableRepository( SearchableRepository<T,String> searchableRepository, Class<T> repositoryDataType, Set<String> searchTokens, Page<T> searchResultsPage, PagedResources<MapItemDto> searchResultsPagedResource )
    {
        Mockito.when( searchableRepository.getResultType() ).thenReturn( repositoryDataType );
        Mockito.when( searchableRepository.search( searchTokens, this.pageable ) ).thenReturn( searchResultsPage );
        Mockito.when( this.itemAssembler.toDtoPage( searchResultsPage ) ).thenReturn( searchResultsPagedResource );
    }
    
    private void initDependencies()
    {
        Mockito.when( this.repositories.iterator() ).thenReturn( this.repositoryIterator );
        Mockito.when( this.repositoryIterator.hasNext() ).thenReturn( true )
                                                         .thenReturn( true )
                                                         .thenReturn( false );
        Mockito.when( this.repositoryIterator.next() ).thenReturn( Node.class )
                                                      .thenReturn( Machine.class )
                                                      .thenReturn( null );
        Mockito.when( this.repositories.getRepositoryFor( Node.class ) ).thenReturn( this.searchableNodeRepository );
        Mockito.when( this.repositories.getRepositoryFor( Machine.class ) ).thenReturn( this.searchableMachineRepository );

        this.configureMockSearchableRepository( this.searchableNodeRepository, Node.class, this.goodSearchQuery.getTokens(), this.nodeSearchPage, this.nodePagedResource );
        this.configureMockSearchableRepository( this.searchableMachineRepository, Machine.class, this.goodSearchQuery.getTokens(), this.machineSearchPage, this.machinePagedResource );
    }
    
    private Map<String, PagedResources<MapItemDto>> newExpectedSearchResults( ExpectedSearchResultsByType... expectedSearchResultsByType )
    {
        HashMap<String, PagedResources<MapItemDto>> expected = new HashMap<String,PagedResources<MapItemDto>>();
        for( ExpectedSearchResultsByType expectedSearchResultsForType : expectedSearchResultsByType )
        {
            expected.put( expectedSearchResultsForType.searchResultsType, expectedSearchResultsForType.searchResults );
        }
        
        return expected;
    }
    
    // TODO improve by adding more test cases, add coverage for paging too     
    
    @Test
    public void positiveTestCase() throws InterruptedException, ExecutionException
    {
        Map<String, PagedResources<MapItemDto>> expected = this.newExpectedSearchResults( new ExpectedSearchResultsByType( Node.class.getSimpleName().toLowerCase(), this.nodePagedResource ), 
                                                                                          new ExpectedSearchResultsByType( Machine.class.getSimpleName().toLowerCase(), this.machinePagedResource ) );
        
        Map<String, PagedResources<MapItemDto>> actual = this.searchEngine.search( this.goodSearchQuery, this.pageable );
        
        Assert.assertEquals( expected, actual );
    }
    
    @Test
    public void negativeTestCase() throws InterruptedException, ExecutionException
    {
        Map<String, PagedResources<MapItemDto>> expected = this.newExpectedSearchResults( new ExpectedSearchResultsByType( Node.class.getSimpleName().toLowerCase(), null ), 
                                                                                          new ExpectedSearchResultsByType( Machine.class.getSimpleName().toLowerCase(), null ) );
        
        Map<String, PagedResources<MapItemDto>> actual = this.searchEngine.search( this.badSearchQuery, this.pageable );
        
        Assert.assertEquals( expected, actual );
    }
    
    
}


