package com.alcadrial.kangu.exception;

public class MessageTooLongException extends Exception {
	
	public MessageTooLongException()
	{}
	
	public MessageTooLongException(String message)
	{
		super(message);
	}
	
	public MessageTooLongException(Throwable cause)
	{
		super(cause);
	}
	
	public MessageTooLongException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
