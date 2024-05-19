package com.pratian.petzey.appointment.Exceptions;

public class AppointmentAlreadyExistException extends RuntimeException {
	public AppointmentAlreadyExistException(String message) {
		super(message);
	}
}
