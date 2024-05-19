package com.pratian.petzey.appointment.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Vital_Details")
public class Vital {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int vitalId;
	private int heartRate;
	private int temperature;
	private int bpm;
}
