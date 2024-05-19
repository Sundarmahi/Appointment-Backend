package com.pratian.petzey.appointment.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyExistException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.InvalidInputDataException;
import com.pratian.petzey.appointment.Exceptions.MicroserviceCallException;

@ControllerAdvice
public class CustomizedExceptionHandler {

	@ExceptionHandler(AppointmentNotFoundException.class)
	public ResponseEntity<Object> appointmentNotFoundHandler(AppointmentNotFoundException notFoundException) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(notFoundException.getMessage());
		responce.setResponceCode(404);
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.NOT_FOUND);
		return responseEntity;
	}

	@ExceptionHandler(AppointmentAlreadyExistException.class)
	public ResponseEntity<Object> appointmentAlreadyExistHandler(
			AppointmentAlreadyExistException alreadyExistException) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(alreadyExistException.getMessage());
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.BAD_REQUEST);
		return responseEntity;

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public Map<String, String> methodArgumentNotValidExceptionHandler1(MethodArgumentNotValidException validException) {
		Map<String, String> errorResponse = new HashMap();
		validException.getBindingResult().getFieldErrors().forEach(error -> {
			errorResponse.put(error.getField(), error.getDefaultMessage());
		});
		return errorResponse;
	}

	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
		return ResponseEntity.badRequest().body("Invalid input format");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.badRequest().body("Invalid input format");
	}

	@ExceptionHandler(DateTimeParseException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
		return ResponseEntity.badRequest().body("Invalid Date format");
	}

	@ExceptionHandler(InvalidInputDataException.class)
	public ResponseEntity<Object> invalidDateExceptionHandler(InvalidInputDataException methodArgumentNotValid) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(methodArgumentNotValid.getLocalizedMessage());
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.BAD_REQUEST);
		return responseEntity;

	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Object> dataAccessExceptionHandler(DataAccessException dataAccess) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(dataAccess.getLocalizedMessage());
		responce.setResponceCode(400);
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.BAD_REQUEST);
		return responseEntity;

	}

	@ExceptionHandler(MicroserviceCallException.class)
	public ResponseEntity<Object> microserviceCallExceptionHandler(MicroserviceCallException dataAccess) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(dataAccess.getLocalizedMessage());
		responce.setResponceCode(404);
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.NOT_FOUND);
		return responseEntity;

	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Object> resourceAccessExceptionHandler(ResourceAccessException dataAccess) {
		ExceptionResponce responce = new ExceptionResponce();
		responce.setDateTime(LocalDate.now());
		responce.setMessage(dataAccess.getLocalizedMessage());
		responce.setResponceCode(404);
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(responce, HttpStatus.NOT_FOUND);
		return responseEntity;

	}

}
