package com.pratian.petzey.appointment.entities;

import java.sql.Date;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Appointments_Details")
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int appointmentId;
	private Date appointmentDate;
	private long vetId;
	private long petParentId;
	private long petId;
	private LocalTime appointment_time;
	private String pet_issues;
	private String reasons_for_visit;
	@Enumerated(EnumType.STRING)
	private AppointmentStatus appointmentStatus;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "report_Id")
	private AppointmentReport appointmentReport;

}
