package com.pratian.petzey.appointment.controller;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyCanceledException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyClosedException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyExistException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackAlreadyExistsException;
import com.pratian.petzey.appointment.Exceptions.FeedbackNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackSubmissionException;
import com.pratian.petzey.appointment.Exceptions.MicroserviceCallException;
import com.pratian.petzey.appointment.Exceptions.VetNotAvailableException;
import com.pratian.petzey.appointment.dto.AppointmentDto;
import org.springframework.web.bind.annotation.RequestMethod;
import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;
import com.pratian.petzey.appointment.entities.Feedback;
import com.pratian.petzey.appointment.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;

@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
		RequestMethod.PATCH }, allowedHeaders = "*")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

	private AppointmentService appointmentService;
	private ModelMapper modelMapper;

	@Autowired
	public AppointmentController(AppointmentService appointmentService, ModelMapper mapper) {
		this.appointmentService = appointmentService;
		this.modelMapper = mapper;
	}

	final static Logger logger = LoggerFactory.getLogger(AppointmentController.class);

	@PostMapping("/save/{vetId}/{petParentId}/{petId}")
	@Operation(summary = "To add new appointments")
	public ResponseEntity<?> postAppointment(@Valid @RequestBody AppointmentDto appointmentDto,
			@PathVariable(value = "vetId") long vetId, @PathVariable(value = "petParentId") long petParentId,
			@PathVariable(value = "petId") long petId) {
		Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
		ResponseEntity<?> response = null;
		try {
			response = new ResponseEntity<>(appointmentService.postAppointment(vetId, petParentId, petId, appointment),
					HttpStatus.CREATED);
			logger.info("Appointment created successfully..Appointment ID:", appointment.getAppointmentId());
		} catch (AppointmentAlreadyExistException | VetNotAvailableException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			logger.error("Appointment creation failed" + e.getLocalizedMessage());
		}
		return response;

	}

	@GetMapping("/getall")
	@Operation(summary = "To get list of all appointments")
	public ResponseEntity<?> getAllAppointments() {
		ResponseEntity<?> response = null;
		try {
			response = new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
			logger.info("All appointments retrieved successfully..");
		} catch (AppointmentNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
			logger.error("Appointment retieval failed..No appointments found");
		}
		return response;
	}

	@GetMapping("/getallByVet/{vetId}")
	public ResponseEntity<List<Appointment>> getAppointmentsByVetId(@PathVariable long vetId) {
		try {
			List<Appointment> appointments = appointmentService.getAppointmentsByVetId(vetId);
			return ResponseEntity.ok().body(appointments);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	@GetMapping("/getallAppointmentByParentId/{petparentId}")
	public ResponseEntity<List<Appointment>> getAppointmentsByPetId(@PathVariable long petparentId) {
		try {
			List<Appointment> appointments = appointmentService.getAppointmentsByPetParentId(petparentId);
			return ResponseEntity.ok().body(appointments);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
	
	@GetMapping("/getallAppointmentByPetId/{petId}")
	public ResponseEntity<List<Appointment>> getAppointmentsBypetId(@PathVariable long petId) {
		try {
			List<Appointment> appointments = appointmentService.getAppointmentsByPetId(petId);
			return ResponseEntity.ok().body(appointments);
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	@GetMapping("/get/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	@Operation(summary = "To get list of appointments by using appointment id")
	public ResponseEntity<?> getAppointmentById(@PathVariable(value = "id") Integer id) {
		ResponseEntity<?> response = null;
		try {
			response = new ResponseEntity<>(appointmentService.getAppointmentById(id), HttpStatus.OK);
			logger.info("Appointment retrieved successfully..Appointment Id is:" + id);

		} catch (AppointmentNotFoundException e) {
			logger.error("Appointment retrieval failed..Appointment ID is:" + id);
			response = new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@GetMapping("/summary/{id}")
	@Operation(summary = "To count confirmed appointments by using appointment id")
	public ResponseEntity<?> getcounts(@PathVariable(value = "id") long id) {
		ResponseEntity<?> response = null;
		response = new ResponseEntity<>(appointmentService.getSummary(id), HttpStatus.OK);
		logger.info("Confirmed appointments count retrieved successfully for appointment ID:" + id);
		return response;
	}

	@PatchMapping("/cancel/{id}")
	@Operation(summary = "To update cancelled appointment by using appointment id")
	public ResponseEntity<String> cancelAppointment(@PathVariable(value = "id") int appointmentId) {
		try {
			logger.warn("Attempting to cancel appointment with id:" + appointmentId);
			appointmentService.cancelAppointment(appointmentId);
			logger.info("Appointment cancelled successfully");
			return ResponseEntity.ok("Appointment canceled successfully");

		} catch (EntityNotFoundException e) {
			logger.error("Appointment not found:" + appointmentId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
		} catch (AppointmentAlreadyCanceledException e) {
			logger.error("Appointment already cancelled:" + appointmentId);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PatchMapping("/close/{id}")
	@Operation(summary = "To update closed appointments by using appointment id")
	public ResponseEntity<String> closeAppointment(@PathVariable(value = "id") int appointmentId) {
		try {
			logger.info("Attempting to close appointment with id: " + appointmentId);
			appointmentService.closeAppointment(appointmentId);
			return ResponseEntity.ok("Appointment closed successfully");
		} catch (EntityNotFoundException e) {
			logger.error("Appointment not found: " + appointmentId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found");
		} catch (AppointmentAlreadyClosedException e) {
			logger.error("Appointment already closed: " + appointmentId);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping("/edit")
	@Operation(summary = "To edit the appointment details")
	public ResponseEntity<?> putAppointment(@RequestBody Appointment appointmet) {
		ResponseEntity<?> response = null;
		System.out.println(appointmet);
		try {
			response = new ResponseEntity<>(appointmentService.putAppointment(appointmet), HttpStatus.OK);
			logger.info("Appointment updated successfully.Appointment ID:" + appointmet.getAppointmentId());
		} catch (AppointmentNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			logger.error(
					"Appointment update failed..Appointment not found.Appointment ID:" + appointmet.getAppointmentId());
		}
		return response;
	}

	@GetMapping("/getStatus/{status}")
	@Operation(summary = "To get all appointment status")
	public ResponseEntity<?> getAllAppointmentsByStatus(@PathVariable(value = "status") AppointmentStatus status) {
		try {
			logger.info("Attempting to get all appointments with status: " + status);
			ResponseEntity<?> response = new ResponseEntity<>(appointmentService.filterAppointments(status),
					HttpStatus.OK);
			logger.info("Found appointments with status: " + status);
			return response;
		} catch (AppointmentNotFoundException e) {
			ResponseEntity<?> responseMessage = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			logger.error("Appointment status not found");
			return responseMessage;
		}

	}

	@GetMapping("/getall/{id}/{date}")
	@Operation(summary = "To get list of all appointment by using appointment ID and date")
	public ResponseEntity<?> getAllAppointmentsByIdAndDate(@PathVariable(value = "id") Integer id,
			@PathVariable(value = "date") String dateString) {
		LocalDate localDate = LocalDate.parse(dateString);
		java.sql.Date date = java.sql.Date.valueOf(localDate);

		ResponseEntity<?> response = null;
		try {
			logger.info("Attempting to get all appointments by id: " + id + " and date: " + date);
			response = new ResponseEntity<>(appointmentService.viewAllAppointmentsByIdAndDate(id, date), HttpStatus.OK);
			logger.info("Found appointments by id: " + id + " and date: " + date);
		} catch (AppointmentNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			logger.error("Appointment not found with given id:" + id + "and date:" + date);
		}
		return response;
	}

	@PostMapping("/feedback/{appointmentId}")
	public ResponseEntity<?> submitFeedback(@PathVariable int appointmentId, @RequestBody Feedback feedback) {
		try {
			Feedback savedFeedback = appointmentService.submitFeedback(appointmentId, feedback);
			return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
		} catch (FeedbackAlreadyExistsException e) {
			return new ResponseEntity<>("Feedback already exists for appointment ID: " + appointmentId,
					HttpStatus.BAD_REQUEST);
		} catch (AppointmentNotFoundException e) {
			return new ResponseEntity<>("Appointment not found for ID: " + appointmentId, HttpStatus.NOT_FOUND);
		} catch (FeedbackSubmissionException e) {
			return new ResponseEntity<>(
					"Feedback submission is not allowed for the appointment with ID: " + appointmentId,
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/feedback")
	public ResponseEntity<List<Feedback>> getAllFeedback() throws FeedbackNotFoundException {
		List<Feedback> feedbackList = appointmentService.getAllFeedback();
		return new ResponseEntity<>(feedbackList, HttpStatus.OK);
	}

	@GetMapping("/getstatusbyvet/{vetId}/{status}")
	@Operation(summary = "To get all appointment status")
	public ResponseEntity<?> getAllAppointmentsByStatusByVet(@PathVariable(value = "vetId") long vetId,
			@PathVariable(value = "status") AppointmentStatus status) {
		try {
			logger.info("Attempting to get all appointments with status: " + status);
			ResponseEntity<?> response = new ResponseEntity<>(appointmentService.filterAppointmentsByVet(vetId, status),
					HttpStatus.OK);
			logger.info("Found appointments with status: " + status);
			return response;
		} catch (AppointmentNotFoundException e) {
			ResponseEntity<?> responseMessage = new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
			logger.error("Appointment there is no Appointment with the status " + status);
			return responseMessage;
		}

	}

	@GetMapping("/getstatusbypet/{petId}/{status}")
	@Operation(summary = "To get all appointment status")
	public ResponseEntity<?> getAllAppointmentsByPetIDStatus(@PathVariable(value = "petId") Integer petId,
			@PathVariable(value = "status") AppointmentStatus status) {
		try {
			logger.info("Attempting to get all appointments with status: " + status);
			ResponseEntity<?> response = new ResponseEntity<>(appointmentService.filterAppointmentsbyPet(petId, status),
					HttpStatus.OK);
			logger.info("Found appointments with status: " + status);
			return response;
		} catch (AppointmentNotFoundException e) {
			ResponseEntity<?> responseMessage = new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
			logger.error("Appointment there is no Appointment with the status " + status);
			return responseMessage;
		}

	}

	@GetMapping("/vet/{id}")
	@Operation(summary = "To get Vet Details  by using  ID ")
	public ResponseEntity<?> getVetById(@PathVariable(value = "id") Long id) {

		ResponseEntity<?> response = null;
		try {
			logger.info("Attempting to get Vet by id: " + id);
			response = new ResponseEntity<>(appointmentService.getVetById(id), HttpStatus.OK);
			logger.info("Found Vet Details by id: " + id);
		} catch (MicroserviceCallException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
			logger.error("Vet not found with given id:" + id);
		}
		return response;
	}

	@GetMapping("/petParent/{id}")
	@Operation(summary = "To get PetParent Details  by using  ID ")
	public ResponseEntity<?> getPetParentById(@PathVariable(value = "id") Long id) {

		ResponseEntity<?> response = null;
		try {
			logger.info("Attempting to get PetParent by id: " + id);
			response = new ResponseEntity<>(appointmentService.getPetParentById(id), HttpStatus.OK);
			logger.info("Found PetParent Details by id: " + id);
		} catch (MicroserviceCallException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
			logger.error("PetParent not found with given id:" + id);
		}
		return response;
	}

//	@GetMapping("/getallvets")
//	@Operation(summary = "To get All Vets Details  ")
//	public ResponseEntity<?> getAllVets() {
//
//		ResponseEntity<?> response = null;
//		try {
//			logger.info("Attempting to get All vets ");
//			response = new ResponseEntity<>(appointmentService.getAllVets(), HttpStatus.OK);
//		} catch (MicroserviceCallException e) {
//			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//			logger.error("Vets not found ");
//		}
//		return response;
//	}

}
