package com.pinelet.common.httpasync;

public class HttpAsyncException extends RuntimeException {

	public enum ExceptionEvent {
		REQASYNC_TIMEOUT, REQASYNC_ERROR, REPLY_ERROR, COMPLELE_ERROR
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final ExceptionEvent exceptionEvent;

	public HttpAsyncException(ExceptionEvent event, Throwable e) {
		super(e);
		this.exceptionEvent = event;
	}

	public ExceptionEvent getExceptionEvent() {
		return exceptionEvent;
	}
}
