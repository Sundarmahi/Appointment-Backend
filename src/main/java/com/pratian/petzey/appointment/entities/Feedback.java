package com.pratian.petzey.appointment.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
public class Feedback {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	@JoinColumn(name = "appointment_id", referencedColumnName = "appointmentId")
	private Appointment appointment;

	@NotNull
	@Max(5)
	private int doctorCompetenceRating;
	@NotNull
	@Max(5)
	private int treatmentOutcomeRating;
	@NotNull
	@Max(5)
	private int referthisdoctorRating;
	@NotNull
	@Max(5)
	private int appointmentProcessRating;

	private String additionalComments;

}