package com.expedia.seiso.domain.repo.search.query.token;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER,
		ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = Validator.class)
public @interface ValidationAnnotation {
	Class<? extends Tokenizer> tokenizer() default SpaceDelimitedDatabaseWildCardTokenizer.class;

	String queryPattern() default "^.{3,500}$";

	String tokenPattern() default "^.{5,50}$";

	String message() default "invalid search query";
}