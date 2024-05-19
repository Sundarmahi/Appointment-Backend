package com.pratian.petzey.appointment.Exceptions;

public class AppointmentAlreadyClosedException extends RuntimeException {

	public AppointmentAlreadyClosedException(String message) {
		super(message);
		
	}

}
