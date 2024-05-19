package com.pratian.petzey.appointment.dto;

import java.util.List;

import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetParentDto {

	private Long petParentId;
	private String petParentName;
	private long phoneNumber;
	private String address;
	private String gender;
	private List<PetDto> pets;

}
