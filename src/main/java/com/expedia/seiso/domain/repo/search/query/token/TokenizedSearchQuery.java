package com.expedia.seiso.domain.repo.search.query.token;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class TokenizedSearchQuery {
	private String query;
	private Set<String> tokens;

	public TokenizedSearchQuery() {
		this(null);
	}

	public TokenizedSearchQuery(String query) {
		this(query, null);
	}

	public TokenizedSearchQuery(String query, Set<String> tokens) {
		this.setQuery(query);
		this.setTokens(tokens);
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public void setTokens(Set<String> tokens) {
		if (this.tokens == null) {
			this.tokens = new LinkedHashSet<String>();
		} else {
			this.tokens.clear();
		}

		if (tokens != null) {
			this.tokens.addAll(tokens);
		}
	}

	public Set<String> getTokens() {
		return Collections.unmodifiableSet(this.tokens);
	}

	public void clearTokens() {
		this.tokens.clear();
	}

}