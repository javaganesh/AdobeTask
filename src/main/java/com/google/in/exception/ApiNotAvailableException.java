package com.google.in.exception;

public class ApiNotAvailableException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiNotAvailableException(String message) {
        super(message);
    }

    public ApiNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
