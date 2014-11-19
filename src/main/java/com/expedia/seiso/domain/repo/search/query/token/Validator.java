package com.expedia.seiso.domain.repo.search.query.token;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class Validator implements ConstraintValidator<ValidationAnnotation, TokenizedSearchQuery> {
	private static final String DEFAULT_QUERY_PARAMETER = "^.{3,500}$";
	private static final String DEFAULT_TOKEN_PATTERN = "^.{5,50}$";
	private static Class<? extends Tokenizer> DEFAULT_TOKENIZER = SpaceDelimitedDatabaseWildCardTokenizer.class;

	private Pattern queryPattern;
	private Pattern tokenPattern;
	private Tokenizer tokenizer;

	public Validator() throws InstantiationException, IllegalAccessException {
		this(Validator.DEFAULT_QUERY_PARAMETER, Validator.DEFAULT_TOKEN_PATTERN, Validator.DEFAULT_TOKENIZER);
	}

	public Validator(String queryPattern, String tokenPattern, Class<? extends Tokenizer> tokenizerClass)
			throws InstantiationException, IllegalAccessException {
		this.initialize(queryPattern, tokenPattern, tokenizerClass);
	}

	public Validator(ValidationAnnotation validationAnnotation) {
		this.initialize(validationAnnotation);
	}

	private void initialize(String queryPattern, String tokenPattern, Class<? extends Tokenizer> tokenizerClass)
			throws InstantiationException, IllegalAccessException {
		Assert.notNull(queryPattern);
		Assert.notNull(tokenPattern);
		Assert.notNull(tokenizerClass);

		this.queryPattern = Pattern.compile(queryPattern);
		this.tokenPattern = Pattern.compile(tokenPattern);
		this.tokenizer = tokenizerClass.newInstance();
	}

	private boolean addConstraintViolation(ConstraintValidatorContext context, String messageTemplate) {
		boolean added = false;

		if (context != null) {
			context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
		}

		return added;
	}

	@Override
	public void initialize(ValidationAnnotation validationAnnotation) {
		Assert.notNull(validationAnnotation);
		try {
			this.initialize(validationAnnotation.queryPattern(), validationAnnotation.tokenPattern(),
					validationAnnotation.tokenizer());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private boolean validateQuery(TokenizedSearchQuery tokenizedSearchQuery, ConstraintValidatorContext context) {
		boolean isValid = true;

		if (tokenizedSearchQuery == null || StringUtils.isEmpty(tokenizedSearchQuery.getQuery())) {
			isValid = false;
			this.addConstraintViolation(context, "empty search query");
		} else if (!this.queryPattern.matcher(tokenizedSearchQuery.getQuery()).matches()) {
			isValid = false;
			this.addConstraintViolation(context, "search query '" + tokenizedSearchQuery.getQuery()
					+ "' does not match pattern " + this.queryPattern);
		}

		return isValid;
	}

	private boolean setValidTokens(TokenizedSearchQuery tokenizedSearchQuery, ConstraintValidatorContext context) {
		boolean isValid = true;

		Set<String> tokens = this.tokenizer.tokenize(tokenizedSearchQuery.getQuery());

		Iterator<String> searchQueryIterator = tokens.iterator();
		while (searchQueryIterator.hasNext()) {
			String searchTerm = searchQueryIterator.next();
			if (!this.tokenPattern.matcher(searchTerm).matches()) {
				searchQueryIterator.remove();
				this.addConstraintViolation(context, "removed search term '" + searchTerm
						+ "' because it does not match pattern " + this.tokenPattern.pattern());
			}
		}

		if (tokens.size() < 1) {
			isValid = false;
			this.addConstraintViolation(context, "no valid search terms");
		}

		tokenizedSearchQuery.setTokens(tokens);

		return isValid;
	}

	@Override
	public boolean isValid(TokenizedSearchQuery tokenizedSearchQuery, ConstraintValidatorContext context) {
		boolean isValid = false;

		if (tokenizedSearchQuery != null) {
			tokenizedSearchQuery.clearTokens();
			isValid = this.validateQuery(tokenizedSearchQuery, context)
					&& this.setValidTokens(tokenizedSearchQuery, context);
		}

		return isValid;
	}
}
