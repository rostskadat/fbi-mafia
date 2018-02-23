package com.stratio.fbi.mafia.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

	private static final Log LOG = LogFactory.getLog(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public String handle(ResourceNotFoundException e) {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = IllegalArgumentException.class)
    public String handle(IllegalArgumentException e) {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
    public String handle(Exception e) {
		LOG.error(e.getMessage(), e);
		return e.getMessage();
	}
}