package com.expedia.seiso.domain.repo.search.query.token;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

public class SpaceDelimitedDatabaseWildCardTokenizer implements Tokenizer
{
    private static final String TERM_DELIMITER = " ";
    private static final String WILD_CARD = "%";
    
    public Set<String> tokenize( String termsString )
    {
        HashSet<String> tokens = new LinkedHashSet<String>();
        
        if( !StringUtils.isEmpty( termsString ) )
        {
            String[] terms = termsString.split( TERM_DELIMITER );
            for( String term : terms )
            {
                if( !StringUtils.isEmpty( term ) )
                {
                    tokens.add( new StringBuilder( WILD_CARD ).append( term.trim() ).append( WILD_CARD ).toString() );
                }
            }
        }
        
        return tokens;
    }
}

