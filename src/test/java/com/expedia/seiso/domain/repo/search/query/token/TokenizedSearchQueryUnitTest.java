package com.expedia.seiso.domain.repo.search.query.token;

import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

public class TokenizedSearchQueryUnitTest {

	@Test
	public void testSetTokens() {
		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery();

		Set<String> expected = new TreeSet<String>();
		expected.add("this");
		expected.add("is");
		expected.add("a");
		expected.add("test");
		tokenizedSearchQuery.setTokens(expected);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testSetNullTokens() {
		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery();

		Set<String> expected = new TreeSet<String>();
		tokenizedSearchQuery.setTokens(null);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testClearTokens() {
		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery();

		Set<String> expected = new TreeSet<String>();
		expected.add("this");
		expected.add("is");
		expected.add("a");
		expected.add("test");
		tokenizedSearchQuery.setTokens(expected);
		Set<String> actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);

		tokenizedSearchQuery.clearTokens();

		expected.clear();
		actual = tokenizedSearchQuery.getTokens();

		Assert.assertNotSame(expected, actual);
		Assert.assertEquals(expected, actual);
	}

}
