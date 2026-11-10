package com.alcadrial.kangu.exception;


public class EmptyMessageException extends RuntimeException {

	public EmptyMessageException()
	{}
	
	public EmptyMessageException(String message)
	{
		super(message);
	}
	
	public EmptyMessageException(Throwable cause)
	{
		super(cause);
	}
	
	public EmptyMessageException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
