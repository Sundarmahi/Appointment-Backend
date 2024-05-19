package com.pratian.petzey.appointment.Petzey_Appointment_Service;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
 
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
 
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyCanceledException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyClosedException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyExistException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackSubmissionException;
import com.pratian.petzey.appointment.Exceptions.UnabletoFetchSummaryException;
import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;
import com.pratian.petzey.appointment.entities.Feedback;
import com.pratian.petzey.appointment.repository.AppointmentRepo;
import com.pratian.petzey.appointment.repository.FeedbackRepo;
import com.pratian.petzey.appointment.serviceimpl.AppointmentServiceImpl;
 
@SpringBootTest
class AppointmentServiceTest {
 
	@MockBean
	private AppointmentRepo appointmentrepo;
 
	@Autowired
	private AppointmentServiceImpl service;
 
	@MockBean
	private FeedbackRepo feedbackRepo;
 
	private Appointment appointment;
	private Feedback feedback=new Feedback();
 
	@Test
	public void testGetSummary_Success() {
		// Mocked data
		int id = 123;
		Map<String, Integer> summaryCounts = new HashMap<>();
		summaryCounts.put("Confirmed", 5);
		summaryCounts.put("Closed", 10);
		summaryCounts.put("Cancelled", 3);
		summaryCounts.put("Total", 18);
 
		// Mocking behavior
		when(appointmentrepo.getSummaryCounts(id)).thenReturn(summaryCounts);
 
		// Method call
		Map<String, Integer> result = service.getSummary(id);
 
		// Assertions
		assertEquals(summaryCounts, result);
		verify(appointmentrepo, times(1)).getSummaryCounts(id);
	}
 
	@Test
	public void testGetSummary_Exception() {
		// Mocked data
		int id = 123;
 
		// Mocking behavior to throw an exception
		when(appointmentrepo.getSummaryCounts(id)).thenThrow(new RuntimeException("Test Exception"));
 
		// Method call and exception handling
		try {
			service.getSummary(id);
		} catch (UnabletoFetchSummaryException e) {
			assertEquals("Unable to fetch count", e.getMessage());
		}
 
		// Verifying interaction
		verify(appointmentrepo, times(1)).getSummaryCounts(id);
	}
 
	@Test
	public void testcancelAppointment_success() {
		int appointmentId = 1;
 
		Appointment appointment = new Appointment();
		appointment.setAppointmentId(appointmentId);
		appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
 
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.of(appointment));
 
		service.cancelAppointment(appointmentId);
		assertEquals(AppointmentStatus.CANCELLED, appointment.getAppointmentStatus());
	}
 
	@Test
	public void testcancelAppointment_notFound() {
		int appointmentId = 1;
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.empty());
 
		assertThrows(AppointmentNotFoundException.class, () -> service.cancelAppointment(appointmentId));
	}
 
	@Test
	public void testcancelAppointment_alreadyCanceled() {
		int appointmentId = 1;
		Appointment appointment = new Appointment();
		appointment.setAppointmentId(appointmentId);
		appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.of(appointment));
 
		assertThrows(AppointmentAlreadyCanceledException.class, () -> service.cancelAppointment(appointmentId));
	}
 
	@Test
	public void testcloseAppointment_success() {
		int appointmentId = 1;
 
		Appointment appointment = new Appointment();
		appointment.setAppointmentId(appointmentId);
		appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
 
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.of(appointment));
 
		service.closeAppointment(appointmentId);
		assertEquals(AppointmentStatus.CLOSED, appointment.getAppointmentStatus());
	}
 
	@Test
	public void testcloseAppointment_notFound() {
		int appointmentId = 1;
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.empty());
 
		assertThrows(AppointmentNotFoundException.class, () -> service.closeAppointment(appointmentId));
	}
 
	@Test
	public void testcloseAppointment_alreadyClosed() {
		int appointmentId = 1;
		Appointment appointment = new Appointment();
		appointment.setAppointmentId(appointmentId);
		appointment.setAppointmentStatus(AppointmentStatus.CLOSED);
		when(appointmentrepo.findById(appointmentId)).thenReturn(java.util.Optional.of(appointment));
 
		assertThrows(AppointmentAlreadyClosedException.class, () -> service.closeAppointment(appointmentId));
	}
 
	@Test
	void testFilterAppointments() {
		AppointmentStatus status = AppointmentStatus.CONFIRMED;
		Appointment appointment1 = new Appointment();
		Appointment appointment2 = new Appointment();
 
		List<Appointment> appointmentList = Arrays.asList(appointment1, appointment2);
		when(appointmentrepo.findByStatus(status)).thenReturn(appointmentList);
 
		List<Appointment> result = service.filterAppointments(status);
 
		assertEquals(appointmentList, result);
		verify(appointmentrepo, times(1)).findByStatus(status);
	}
 
	@Test
	void testFilterAppointments_NoAppointmentsFound() {
		AppointmentStatus status = AppointmentStatus.CONFIRMED;
		when(appointmentrepo.findByStatus(status)).thenReturn(new ArrayList<>());
 
		assertThrows(AppointmentNotFoundException.class, () -> service.filterAppointments(status));
		verify(appointmentrepo, times(1)).findByStatus(status);
	}
 
	@Test
	public void testgetAppointmentById() throws AppointmentNotFoundException {
		Appointment appointment = new Appointment();
		appointment.setAppointmentId(1000);
		Optional<Appointment> appointment1 = Optional.of(appointment);
		Mockito.when(appointmentrepo.findById(100)).thenReturn(appointment1);
		int appointmentId = 1000;
		Optional<Appointment> appointment2 = service.getAppointmentById(100);
		Appointment appointment3 = appointment2.get();
		assertEquals(appointmentId, appointment3.getAppointmentId());
	}
 
	@Test
	public void testgetAppointmentByIdException() throws AppointmentNotFoundException {
		when(appointmentrepo.findById(1)).thenThrow(new AppointmentNotFoundException(""));
		assertThrows(AppointmentNotFoundException.class, () -> {
		service.getAppointmentById(1);
		});
	}
 
	@Before(value = "submitFeedback_appointmentNotFound()")
	    public void setUp() {
	       
	        when(appointmentrepo.findById(1)).thenReturn(Optional.of(appointment));
	        when(appointmentrepo.existsById(1)).thenReturn(true);
	        when(feedbackRepo.save(any(Feedback.class))).thenReturn(feedback);
	        when(feedbackRepo.existsByAppointment_AppointmentId(1)).thenReturn(false);
	    }
 
	@Test
	    public void submitFeedback_appointmentNotFound() {
	        when(appointmentrepo.findById(1)).thenReturn(Optional.empty());
	        assertThrows(AppointmentNotFoundException.class, () -> service.submitFeedback(1, feedback));
	    }
 
	@Test
	    public void submitFeedback_feedbackSubmissionException() {
	        when(appointmentrepo.findById(1)).thenReturn(Optional.of(new Appointment()));
	        assertThrows(FeedbackSubmissionException.class, () -> service.submitFeedback(1, feedback));
	    }
//
//	@Test
// 	    public void getAllFeedback_success() {
// 	        when(service.getAllFeedback()).thenReturn(Arrays.asList(feedback));
// 	        List<Feedback> result = service.getAllFeedback();
// 	        assertEquals(1, result.size());
// 	        assertEquals(feedback, result.get(0));
// 	    }
 
	@Test
	public void testGetAllAppointmentsSuccess() {
 
		List<Appointment> appointments = new ArrayList<>();
		Appointment appointment1 = new Appointment();
		Appointment appointment2 = new Appointment();
		appointment1.setPetId(1);
		appointments.add(appointment1);
		appointments.add(appointment2);
 
		System.out.print(appointment1);
		when(appointmentrepo.findAll()).thenReturn(appointments);
 
		List<Appointment> result = service.getAllAppointments();
 
		assertEquals(appointments, result);
	}
 
	@Test
	    public void testGetAllAppointmentsNotFound() {
	        
	        when(appointmentrepo.findAll()).thenThrow(new AppointmentNotFoundException(" "));
	        assertThrows(AppointmentNotFoundException.class, () -> {
	        	service.getAllAppointments();
	        });
	    }
 

 
}