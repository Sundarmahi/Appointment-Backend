package com.pratian.petzey.appointment.serviceimpl;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyCanceledException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyClosedException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyExistException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackAlreadyExistsException;
import com.pratian.petzey.appointment.Exceptions.FeedbackSubmissionException;
import com.pratian.petzey.appointment.Exceptions.MicroserviceCallException;
import com.pratian.petzey.appointment.Exceptions.UnabletoFetchSummaryException;
import com.pratian.petzey.appointment.Exceptions.VetNotAvailableException;
import com.pratian.petzey.appointment.dto.PetParentDto;
import com.pratian.petzey.appointment.dto.VetDto;
import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;
import com.pratian.petzey.appointment.entities.Feedback;
import com.pratian.petzey.appointment.repository.AppointmentRepo;
import com.pratian.petzey.appointment.repository.AppointmentReportRepo;
import com.pratian.petzey.appointment.repository.FeedbackRepo;
import com.pratian.petzey.appointment.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private AppointmentRepo appointmentRepo;
	private AppointmentReportRepo appointmentReportRepo;
	private FeedbackRepo feedbackRepo;
	private RestTemplate restTemplate;

	// constructor
	@Autowired
	public AppointmentServiceImpl(AppointmentRepo appointmentRepo, AppointmentReportRepo appointmentReportRepo,
			RestTemplate restTemplate, FeedbackRepo feedbackRepo) {

		this.appointmentRepo = appointmentRepo;
		this.appointmentReportRepo = appointmentReportRepo;
		this.restTemplate = restTemplate;
		this.feedbackRepo = feedbackRepo;
	}

	// post
	@Override
	public Appointment postAppointment(long vetId, long petParentId, long petId, Appointment appointment) {

		if (appointmentRepo.existsById(appointment.getAppointmentId())) {
			throw new AppointmentAlreadyExistException("Appointment Already Exist");
		}

		// if (validateDate(appointment.getAppointmentDate()) &&
		// validateTime(appointment.getAppointment_time())) {
		// throw new InvalidInputDataException("Appointment Date Should be Greater than
		// Current Date");
		// }
		List<Appointment> appointments = appointmentRepo.checkVets(vetId, appointment.getAppointmentDate());

		System.out.println(appointments);

		for (Appointment appointment2 : appointments) {
			if (appointment2.getAppointment_time().toString().equals(appointment.getAppointment_time().toString())) {
				throw new VetNotAvailableException("Vet Not Available For This Schedule");
			}
		}
		appointment.setPetId(petId);
		appointment.setVetId(vetId);
		appointment.setPetParentId(petParentId);
		appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
		appointmentRepo.save(appointment);
		return appointment;

	}

	@Override
	public List<Appointment> getAllAppointments() {

		List<Appointment> appointments = appointmentRepo.findAll();
		if (appointments.isEmpty()) {
			throw new AppointmentNotFoundException("Appointments Not Found");
		}

		return appointments;
	}

	@Override
	public Optional<Appointment> getAppointmentById(Integer id) {
		Optional<Appointment> appointment = appointmentRepo.findById(id);
		if (!appointment.isPresent()) {
			throw new AppointmentNotFoundException("Appointment Not Found For Id :" + id);
		}

		return appointment;
	}

	@Override
	public Map<String, Integer> getSummary(long id) {
		try {
			return appointmentRepo.getSummaryCounts(id);
		} catch (Exception e) {
			throw new UnabletoFetchSummaryException("Unable to fetch count");
		}
	}

	@Override
	public void cancelAppointment(int appointmentId) throws AppointmentAlreadyCanceledException {
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id:" + appointmentId));

		if (appointment.getAppointmentStatus().equals(AppointmentStatus.CONFIRMED)) {
			appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
			appointmentRepo.save(appointment);
		} else if (appointment.getAppointmentStatus().equals(AppointmentStatus.CLOSED)) {
			throw new AppointmentAlreadyCanceledException("This appointment is already closed");
		} else {
			throw new AppointmentAlreadyCanceledException("Appointment is already canceled");
		}
	}

	@Override
	public void closeAppointment(int appointmentId) throws AppointmentAlreadyClosedException {
		System.out.println("called");
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id:" + appointmentId));
		if (appointment.getAppointmentStatus().equals(AppointmentStatus.CONFIRMED)) {
			appointment.setAppointmentStatus(AppointmentStatus.CLOSED);
			appointmentRepo.save(appointment);
		} else if (appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
			throw new AppointmentAlreadyClosedException("This appointment is already canceled");
		} else {
			throw new AppointmentAlreadyClosedException("Appointment is already closed");
		}
	}

	@Override
	public Appointment putAppointment(Appointment appointment) {
		if (appointmentRepo.existsById(appointment.getAppointmentId())) {
			return appointmentRepo.save(appointment);
		}
		throw new AppointmentNotFoundException("Appointment Not Found For ID " + appointment.getAppointmentId());
	}

	@Override
	public List<Appointment> filterAppointments(AppointmentStatus status) {
		List<Appointment> appointmentStatus = appointmentRepo.findByStatus(status);
		if (appointmentStatus.isEmpty()) {
			throw new AppointmentNotFoundException("Appointments Not Found for the selected status:" + status);
		}
		return appointmentStatus;
	}

	@Override
	public List<Appointment> viewAllAppointmentsByIdAndDate(Integer id, Date date) {
		List<Appointment> appointments = appointmentRepo.findByIdAndDate(id, date);
		if (appointments.isEmpty()) {
			throw new AppointmentNotFoundException("Appointments Not Found");
		}
		return appointments;
	}

	@Override
	public Feedback submitFeedback(int appointmentId, Feedback feedback)
			throws FeedbackAlreadyExistsException, AppointmentNotFoundException, FeedbackSubmissionException {
		Appointment appointment = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new AppointmentNotFoundException("Appointment not found for ID: " + appointmentId));

		if (appointment.getAppointmentStatus() != AppointmentStatus.CLOSED) {
			throw new FeedbackSubmissionException(
					"Feedback can only be submitted for appointments with a status of CLOSED");
		}

		if (isFeedbackExistsForAppointment(appointmentId)) {
			throw new FeedbackAlreadyExistsException("Feedback already exists for appointment ID: " + appointmentId);
		}

		feedback.setAppointment(appointment);
		return feedbackRepo.save(feedback);
	}

	@Override
	public List<Feedback> getAllFeedback() {
		return feedbackRepo.findAll();
	}

	public boolean isFeedbackExistsForAppointment(int appointmentId) {
		return feedbackRepo.existsByAppointment_AppointmentId(appointmentId);
	}

	private boolean validateTime(LocalTime appointmentTime) {
		LocalTime currentTime = LocalTime.now();
		if (appointmentTime.getHour() <= currentTime.getHour()) {
			return false;
		}
		return true;
	}

	public List<Appointment> getAppointmentsByVetId(long vetId) {
	   System.out.println(appointmentRepo.findByVetId(vetId));
	 return  appointmentRepo.findByVetId(vetId);
	}

	public List<Appointment> filterAppointmentsByVet(long vetId, AppointmentStatus status) {
		List<Appointment> appointmentStatus = appointmentRepo.findByStatusByVet(vetId, status);
		if (appointmentStatus.isEmpty()) {
			throw new AppointmentNotFoundException("Appointments Not Found for the selected status:" + status);
		}
		return appointmentStatus;
	}

	@Override
	public List<Appointment> filterAppointmentsbyPet(long petId, AppointmentStatus status) {
		List<Appointment> appointmentStatus = appointmentRepo.findByStatusByPet(petId, status);
		if (appointmentStatus.isEmpty()) {
			throw new AppointmentNotFoundException("Appointments Not Found for the selected status:" + status);
		}
		return appointmentStatus;
	}

	private boolean validateDate(Date appointmentDate) {
		java.util.Date date = java.util.Date.from(Instant.now());
		if ((appointmentDate.getYear() < date.getYear()) && (appointmentDate.getDay() < date.getDay())) {
			return false;
		}
		return true;
	}

	@Override
	public VetDto getVetById(long id) {
		VetDto vetDto = restTemplate.getForObject("https://apigateway.bt.skillassure.com/Vet/api/vet/" + id,
				VetDto.class);
		Optional<VetDto> vetDto2 = Optional.of(vetDto);
		if (vetDto2.isPresent()) {
			return vetDto;
		}

		throw new MicroserviceCallException("Vet Details Not Found For Id :" + id);
	}

	@Override
	public PetParentDto getPetParentById(long id) {
		PetParentDto PetParentDto = restTemplate
				.getForObject("https://apigateway.bt.skillassure.com/pet/pet/getParentByID/" + id, PetParentDto.class);
		Optional<PetParentDto> PetParentDto2 = Optional.of(PetParentDto);
		if (PetParentDto2.isPresent() && !PetParentDto2.isEmpty()) {
			return PetParentDto;
		}
		throw new MicroserviceCallException("PetParent Details Not Found For Id :" + id);

	}

	@Override
	public List<Appointment> getAppointmentsByPetParentId(long petParentId) {
		return appointmentRepo.findByPetparentId(petParentId);
	}
	
	@Override
	public List<Appointment> getAppointmentsByPetId(long petId) {
		return appointmentRepo.findByPetId(petId);
	}

}
