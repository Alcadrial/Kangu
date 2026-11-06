package com.alcadrial.kangu.exception;

public class MessagesCursorNotFoundException extends RuntimeException {
	
	public MessagesCursorNotFoundException()
	{}
	
	public MessagesCursorNotFoundException(String message)
	{
		super(message);
	}
	
	public MessagesCursorNotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public MessagesCursorNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
