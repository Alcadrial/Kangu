package com.alcadrial.kafkatest.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.alcadrial.kafkatest.utils.Validation;

class ValidationTest {
	
	@ParameterizedTest
	@ValueSource(strings = {"g@g.g", "g@g", "acme@example.com", "air.jet.konne@acme.inc"})
	void validEmails(String email)
	{
		assertTrue(Validation.email(email));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {".g@g", "g@g.", "g..g@g", "g@g..g", "g.@.g", "akka", "don';DROP DATABASE;--@good.inc"})
	void invalidEmails(String email)
	{
		assertFalse(Validation.email(email));
	}
}
