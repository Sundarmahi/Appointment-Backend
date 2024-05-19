package com.pratian.petzey.appointment.dto;

import java.util.Date;

import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
	private Long petId;
	private String petName;
	private String breed;
	private String gender;
	private Integer age;
	private String bloodGroup;
	private Date dateOfBirth;
	private PetParentDto petParent;

}
