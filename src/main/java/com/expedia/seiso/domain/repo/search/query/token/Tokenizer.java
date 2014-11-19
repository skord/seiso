package com.expedia.seiso.domain.repo.search.query.token;

import java.util.Set;

public interface Tokenizer {
	public Set<String> tokenize(String termsString);
}
