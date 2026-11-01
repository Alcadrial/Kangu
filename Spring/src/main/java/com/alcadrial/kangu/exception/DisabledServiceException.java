package com.alcadrial.kangu.exception;

public class DisabledServiceException extends Exception {
	
	public DisabledServiceException()
	{}
	
	public DisabledServiceException(String message)
	{
		super(message);
	}
	
	public DisabledServiceException(Throwable cause)
	{
		super(cause);
	}
	
	public DisabledServiceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
