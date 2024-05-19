package com.pratian.petzey.appointment.controller;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponce {
	private String message;
	private LocalDate dateTime;
	private int responceCode;

}
