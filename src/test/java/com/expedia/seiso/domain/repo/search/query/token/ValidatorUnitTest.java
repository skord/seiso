package com.expedia.seiso.domain.repo.search.query.token;

import javax.validation.ConstraintValidatorContext;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ValidatorUnitTest {
	private static String goodQuery;
	private static String queryPassesQueryPatternButNotTokensPattern;
	private static String queryFailsQueryPattern;

	private static @Mock ConstraintValidatorContext constraintValidatorContext;

	@BeforeClass
	public static void setup() {
		MockitoAnnotations.initMocks(ValidatorUnitTest.class);

		ValidatorUnitTest.goodQuery = "good query";
		ValidatorUnitTest.queryFailsQueryPattern = "a";
		ValidatorUnitTest.queryPassesQueryPatternButNotTokensPattern = "a b c";
	}

	// TODO improve tests to inspect constraints as well as boolean result

	@Test
	public void testNull() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		TokenizedSearchQuery tokenizedSearchQuery = null;
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorUnitTest.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testEmpty() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery();
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorUnitTest.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGoodQueryAndTokens() throws InstantiationException, IllegalAccessException {
		boolean expected = true;

		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery(ValidatorUnitTest.goodQuery);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorUnitTest.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testBadQuery() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery(ValidatorUnitTest.queryFailsQueryPattern);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorUnitTest.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testBadTokens() throws InstantiationException, IllegalAccessException {
		boolean expected = false;

		TokenizedSearchQuery tokenizedSearchQuery = new TokenizedSearchQuery(
				ValidatorUnitTest.queryPassesQueryPatternButNotTokensPattern);
		Validator validator = new Validator();
		boolean actual = validator.isValid(tokenizedSearchQuery, ValidatorUnitTest.constraintValidatorContext);

		Assert.assertEquals(expected, actual);
	}

}
