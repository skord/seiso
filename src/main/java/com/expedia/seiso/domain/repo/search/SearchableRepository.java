package com.expedia.seiso.domain.repo.search;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchableRepository<T, ID extends Serializable>
{
    Class<T> getResultType();
    Page<T>  search( Set<String> searchTokens, Pageable pageable );
}
