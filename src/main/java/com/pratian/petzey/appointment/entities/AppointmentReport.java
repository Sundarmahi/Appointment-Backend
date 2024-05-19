package com.pratian.petzey.appointment.entities;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Appointment_Reports")
public class AppointmentReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;
	@OneToMany(cascade = CascadeType.ALL)
	private List<Prescription> prescriptions;
	@ElementCollection(targetClass = String.class)
	private List<String> tests;
	@ElementCollection(targetClass = String.class)
	private List<String> symptoms;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "vital_Id")
	private Vital vitals;
	@ElementCollection(targetClass = String.class)
	private List<String> recommendedClinics;
	@ElementCollection
	private List<String> recommendedDoctors;
	private String comments;


}
