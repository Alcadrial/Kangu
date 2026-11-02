package com.alcadrial.kangu;

import java.util.NoSuchElementException;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alcadrial.kangu.exception.BlockedException;
import com.alcadrial.kangu.exception.DisabledServiceException;
import com.alcadrial.kangu.exception.DuplicateKeyException;
import com.alcadrial.kangu.exception.MessageTooLongException;
import com.alcadrial.kangu.exception.UserNotFoundException;
import com.alcadrial.kangu.utils.Constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionHandlerController {
	
	public record ErrorResponse(String errorCode, String message) {}
	
	final Environment env;
	
	@ExceptionHandler
	ResponseEntity<?> handler(BadCredentialsException e)
	{
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(Constants.BAD_CREDENTIALS_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(DisabledException e)
	{
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(Constants.DISABLED_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(LockedException e)
	{
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(Constants.LOCKED_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(DuplicateKeyException e)
	{
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(Constants.DUPLICATED_VALUE_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(DisabledServiceException e)
	{
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(Constants.DISABLED_SERVICE_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(MessageTooLongException e)
	{
		return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(new ErrorResponse(Constants.VALUE_TOO_BIG_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(UserNotFoundException e)
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(Constants.NOT_FOUND_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(BlockedException e)
	{
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(Constants.BLOCKED_CHAT_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> handler(NoSuchElementException e)
	{
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(Constants.NOT_FOUND_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
	
	@ExceptionHandler
	ResponseEntity<?> unknownErrorRequest(Exception e)
	{
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(Constants.KNOWN_INTERNAL_ERROR_CODE, Constants.EMPTY_MESSAGE));
	}
}
