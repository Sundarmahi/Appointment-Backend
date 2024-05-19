package com.pratian.petzey.appointment.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VetDto {

	private long vetId;

	private String vetName;

	private String mobileNo;

	private String email;

	private String speciality;

	private int rating;

	private List<String> dailySchedule = new ArrayList();

	private String myClinic;
}
