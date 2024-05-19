package com.pratian.petzey.appointment.dto;

import java.sql.Date;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import com.pratian.petzey.appointment.entities.AppointmentReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

	@NotNull(message = "Appointment Date Should Not Be Null")
	private Date appointmentDate;
	@NotNull(message = "Appointment Time Should Not Be Null")
	private LocalTime appointment_time;
	private String pet_issues;
	private String reasons_for_visit;
	private AppointmentReport appointmentReport;
}
