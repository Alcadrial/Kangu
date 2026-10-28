package com.alcadrial.kangu.exception;

public class BlockedException extends Exception {
	
	public BlockedException()
	{}
	
	public BlockedException(String message)
	{
		super(message);
	}
	
	public BlockedException(Throwable cause)
	{
		super(cause);
	}
	
	public BlockedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
