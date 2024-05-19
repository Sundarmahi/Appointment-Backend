package com.pratian.petzey.appointment.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Prescriptions")
public class Prescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int prescriptionId;
	private String medicineName;
	private int noOfDaysOfConsumption;
	private String advice;
	@Enumerated(EnumType.STRING)
	private ConsumptionSchedule consumptionSchedule;
	@Enumerated(EnumType.STRING)
	private ConsumptionTiming consumptionTiming;

}
