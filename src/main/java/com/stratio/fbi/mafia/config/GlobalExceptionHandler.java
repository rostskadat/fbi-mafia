package com.stratio.fbi.mafia.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple REST Exception Handler
 * 
 * @author rostskadat
 *
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

	private static final Log LOG = LogFactory.getLog(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = FileNotFoundException.class)
	public String handle(FileNotFoundException e) throws IOException {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = IllegalArgumentException.class)
	public String handle(IllegalArgumentException e) throws IOException {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
	public String handle(Exception e) throws IOException {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}
}