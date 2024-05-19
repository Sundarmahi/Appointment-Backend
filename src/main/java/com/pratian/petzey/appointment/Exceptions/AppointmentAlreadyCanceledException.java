package com.pratian.petzey.appointment.Exceptions;

public class AppointmentAlreadyCanceledException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppointmentAlreadyCanceledException(String message) {
		super(message);
	}

}
