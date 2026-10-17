package com.alcadrial.kafkatest.exception;

public class DuplicateKeyException extends Exception {
	
	public DuplicateKeyException()
	{}
	
	public DuplicateKeyException(String message)
	{
		super(message);
	}
	
	public DuplicateKeyException(Throwable cause)
	{
		super(cause);
	}
	
	public DuplicateKeyException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
