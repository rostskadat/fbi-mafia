package com.stratio.fbi.mafia.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.stratio.fbi.mafia.exception.ResourceNotFoundException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Log LOG = LogFactory.getLog(GlobalExceptionHandler.class);

    @ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ApiError handle(ResourceNotFoundException e) {
		LOG.error(e.getMessage(), e);
        return new ApiError("INPUT", e.getMessage());
	}

    @ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = IllegalArgumentException.class)
    public ApiError handle(IllegalArgumentException e) {
		LOG.error(e.getMessage(), e);
        return new ApiError("REQUEST", e.getMessage());
	}

    @ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
    public ApiError handle(Exception e) {
		LOG.error(e.getMessage(), e);
        return new ApiError("NA", e.getMessage());
	}

    public static class ApiError {
        private String field;
        private String message;

        ApiError() {
        }

        ApiError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}