package com.pratian.petzey.appointment.Petzey_Appointment_Service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyCanceledException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyClosedException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackAlreadyExistsException;
import com.pratian.petzey.appointment.Exceptions.FeedbackSubmissionException;
import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;
import com.pratian.petzey.appointment.entities.Feedback;
import com.pratian.petzey.appointment.service.AppointmentService;

@WebMvcTest
public class AppointmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AppointmentService appointmentService;

	private static ObjectMapper mapper = new ObjectMapper();

	// Cancel Appointment - Positive and Negative Test Cases
	@Test
	public void testcancelAppointment() throws Exception {
		int appointmentId = 123;
		mockMvc.perform(patch("/appointment/cancel/{id}", appointmentId)).andExpect(status().isOk())
				.andExpect(content().string("Appointment canceled successfully"));
	}

	@Test
	void testcancelAppointment_InvalidAppointmentId() throws Exception {
		int appointmentId = 001; // Use an invalid appointment ID
		Mockito.doThrow(new javax.persistence.EntityNotFoundException("Appointment not found")).when(appointmentService)
				.cancelAppointment(appointmentId);
		mockMvc.perform(patch("/appointment/cancel/{id}", appointmentId)).andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Appointment not found")));
	}

	@Test
	void testcancelAppointment_AlreadyCanceled() throws Exception {
		int appointmentId = 456;
		Mockito.doThrow(new AppointmentAlreadyCanceledException("Appointment is already canceled"))
				.when(appointmentService).cancelAppointment(appointmentId);
		mockMvc.perform(patch("/appointment/cancel/{id}", appointmentId)).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Appointment is already canceled")));
	}

	// Close Appointment - Positive and Negative Test Cases
	@Test
	public void testcloseAppointment() throws Exception {
		int appointmentId = 123;
		mockMvc.perform(patch("/appointment/close/{id}", appointmentId)).andExpect(status().isOk())
				.andExpect(content().string("Appointment closed successfully"));
	}

	@Test
	void testcloseAppointment_InvalidAppointmentId() throws Exception {
		int appointmentId = 001; // Use an invalid appointment ID
		Mockito.doThrow(new javax.persistence.EntityNotFoundException("Appointment not found")).when(appointmentService)
				.closeAppointment(appointmentId);
		mockMvc.perform(patch("/appointment/close/{id}", appointmentId)).andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Appointment not found")));
	}

	@Test
	void testcloseAppointment_AlreadyClosed() throws Exception {
		int appointmentId = 456;
		Mockito.doThrow(new AppointmentAlreadyClosedException("Appointment is already closed")).when(appointmentService)
				.closeAppointment(appointmentId);
		mockMvc.perform(patch("/appointment/close/{id}", appointmentId)).andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Appointment is already closed")));
	}

	// Getting the count
	@Test
	void testGetSummarySuccess() throws Exception {
		int id = 1;
		Map<String, Integer> summary = new HashMap<>();
		summary.put("Cancelled", 10);
		summary.put("Confirmed", 20);
		summary.put("Closed", 30);
		summary.put("Total", 40);
		when(appointmentService.getSummary(id)).thenReturn(summary);
 
		mockMvc.perform(get("/appointment//summary/{id}", id)).andExpect(status().isOk())
				.andExpect(jsonPath("$.Cancelled", is(10))).andExpect(jsonPath("$.Confirmed", is(20)))
				.andExpect(jsonPath("$.Closed", is(30))).andExpect(jsonPath("$.Total", is(40)));
	}

	@Test
	public void getAllAppointmentsByIdAndDate_success() throws Exception {
		// Given
		Integer id = 1;
		String dateString = "2022-01-01";
		LocalDate localDate = LocalDate.parse(dateString);
		java.sql.Date date = java.sql.Date.valueOf(localDate);
		List<Appointment> appointments = new ArrayList<>();
		appointments.add(new Appointment());
		when(appointmentService.viewAllAppointmentsByIdAndDate(id, date)).thenReturn(appointments);

		// When
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/getall/" + id + "/" + dateString);
		ResultActions resultActions = mockMvc.perform(requestBuilder);

	}

	@Test
	public void testgetAppointmentById() throws Exception {

		List<Appointment> appointments = new ArrayList<>();
		Appointment appointment = new Appointment();

		Appointment appointment1 = new Appointment();
		Appointment appointment2 = new Appointment();
		Optional<Appointment> appointment3 = Optional.of(new Appointment());
		appointment.setAppointmentId(1001);
		appointment1.setAppointmentId(1002);
		appointment2.setAppointmentId(1003);

		appointments.add(appointment);
		appointments.add(appointment1);
		appointments.add(appointment2);

		when(appointmentService.getAppointmentById(1004)).thenReturn(appointment3);
		mockMvc.perform(get("/appointment/get/1004")).andExpect(status().isOk());

	}

	@Test
	public void testgetAppointmentByIdException() throws Exception {

		List<Appointment> appointments = new ArrayList<>();
		Appointment appointment1 = new Appointment();
		Appointment appointment2 = new Appointment();
		appointment1.setAppointmentId(2002);
		appointment2.setAppointmentId(2003);
		appointments.add(appointment2);
		appointments.add(appointment1);
		System.out.println(appointments);
		when(appointmentService.getAppointmentById(200)).thenThrow(new AppointmentNotFoundException(""));
		mockMvc.perform(get("/appointment/get/200")).andExpect(status().isNotFound());

	}
	//

	@Test
	public void submitFeedback_success() throws Exception {
		int appointmentId = 1;
		Feedback feedback = new Feedback();
		Feedback savedFeedback = new Feedback();
		savedFeedback.setId(1);

		when(appointmentService.submitFeedback(appointmentId, feedback)).thenReturn(savedFeedback);

		mockMvc.perform(post("/appointment/feedback/{appointmentId}", appointmentId)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(feedback)))
				.andExpect(status().isCreated());
		// .andExpect(jsonPath("$.feedbackId", is(1)));

		verify(appointmentService, times(1)).submitFeedback(appointmentId, feedback);
	}

	@Test
	public void submitFeedback_feedbackAlreadyExists() throws Exception {
		int appointmentId = 1;
		Feedback feedback = new Feedback();

		when(appointmentService.submitFeedback(appointmentId, feedback)).thenThrow(
				new FeedbackAlreadyExistsException("Feedback already exists for appointment ID: " + appointmentId));

		mockMvc.perform(post("/appointment/feedback/{appointmentId}", appointmentId)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(feedback)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Feedback already exists for appointment ID: 1"));

		verify(appointmentService, times(1)).submitFeedback(appointmentId, feedback);
	}

	@Test
	public void submitFeedback_appointmentNotFound() throws Exception {
		int appointmentId = 1;
		Feedback feedback = new Feedback();

		when(appointmentService.submitFeedback(appointmentId, feedback))
				.thenThrow(new AppointmentNotFoundException("Appointment not found for ID: " + appointmentId));

		mockMvc.perform(post("/appointment/feedback/{appointmentId}", appointmentId)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(feedback)))
				.andExpect(status().isNotFound()).andExpect(content().string("Appointment not found for ID: 1"));

		verify(appointmentService, times(1)).submitFeedback(appointmentId, feedback);
	}

	@Test
	public void submitFeedback_feedbackSubmissionException() throws Exception {
		int appointmentId = 1;
		Feedback feedback = new Feedback();

		when(appointmentService.submitFeedback(appointmentId, feedback)).thenThrow(new FeedbackSubmissionException(
				"Feedback submission is not allowed for the appointment with ID: " + appointmentId));

		mockMvc.perform(post("/appointment/feedback/{appointmentId}", appointmentId)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(feedback)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Feedback submission is not allowed for the appointment with ID: 1"));

		verify(appointmentService, times(1)).submitFeedback(appointmentId, feedback);
	}

	@Test
	public void testGetAllFeedback() throws Exception {
		List<Feedback> feedbackList = new ArrayList<>();

		when(appointmentService.getAllFeedback()).thenReturn(feedbackList);

		mockMvc.perform(get("/appointment/feedback").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(feedbackList.size()));
	}

	@Test
	public void getAllAppointmentsByStatus_success() throws Exception {
		// Given
		AppointmentStatus status = AppointmentStatus.CONFIRMED;
		List<Appointment> appointments = new ArrayList<>();
		appointments.add(new Appointment());
		when(appointmentService.filterAppointments(status)).thenReturn(appointments);

		// When
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment/getStatus/" + status);
		ResultActions resultActions = mockMvc.perform(requestBuilder);
///
		// Then
		resultActions.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void getAllAppointmentsByStatus_notFound() throws Exception {
		// Given
		AppointmentStatus status = AppointmentStatus.CANCELLED;
		when(appointmentService.filterAppointments(status))
				.thenThrow(new AppointmentNotFoundException("Appointment status not found"));

		// When
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/appointment/getStatus/" + status);
		ResultActions resultActions = mockMvc.perform(requestBuilder);

		// Then
		resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("Appointment status not found"));
	}

	@Test
	public void testGetAllAppointment() throws Exception {
		List<Appointment> getAllAppoointments = new ArrayList<>();
		List<java.lang.String> allergies = new ArrayList<>();
		allergies.add("nose");
		Appointment testAppointment = new Appointment();
		testAppointment.setAppointmentId(1);
		testAppointment.setAppointmentDate(new Date(2012 - 12 - 10));
		testAppointment.setAppointmentStatus(AppointmentStatus.CLOSED);
		testAppointment.setPetId(1);
		testAppointment.setVetId(1);

		getAllAppoointments.add(testAppointment);
		System.out.println(testAppointment);

		System.out.println(getAllAppoointments.size() + " hell");

		when(appointmentService.getAllAppointments()).thenReturn(getAllAppoointments);

		mockMvc.perform(get("/appointment/getall")).andExpect(status().isOk());

	}

	@Test
	    public void testGetAllAppointmentsNotFound() throws Exception {
	        when(appointmentService.getAllAppointments()).thenThrow(new AppointmentNotFoundException("No appointments found"));
	        mockMvc.perform(get("/getall"))
	               .andExpect(status().isNotFound());
	    }


}