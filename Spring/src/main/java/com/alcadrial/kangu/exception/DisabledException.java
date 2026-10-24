package com.alcadrial.kangu.exception;

public class DisabledException extends Exception {
	
	public DisabledException()
	{}
	
	public DisabledException(String message)
	{
		super(message);
	}
	
	public DisabledException(Throwable cause)
	{
		super(cause);
	}
	
	public DisabledException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
