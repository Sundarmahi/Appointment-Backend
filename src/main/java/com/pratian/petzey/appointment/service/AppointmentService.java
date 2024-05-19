package com.pratian.petzey.appointment.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyCanceledException;
import com.pratian.petzey.appointment.Exceptions.AppointmentAlreadyClosedException;
import com.pratian.petzey.appointment.Exceptions.AppointmentNotFoundException;
import com.pratian.petzey.appointment.Exceptions.FeedbackAlreadyExistsException;
import com.pratian.petzey.appointment.Exceptions.FeedbackSubmissionException;
import com.pratian.petzey.appointment.dto.PetParentDto;
import com.pratian.petzey.appointment.dto.VetDto;
import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;
import com.pratian.petzey.appointment.entities.Feedback;

public interface AppointmentService {

	public Appointment postAppointment(long vetId, long petParentId, long petId, Appointment appointment);

	public List<Appointment> getAllAppointments();

	public Optional<Appointment> getAppointmentById(Integer id);

	public Map<String, Integer> getSummary(long id);

	public void cancelAppointment(int appointmentId) throws AppointmentAlreadyCanceledException;

	public void closeAppointment(int appointmentId) throws AppointmentAlreadyClosedException;

	public Appointment putAppointment(Appointment appointment);

	public List<Appointment> filterAppointments(AppointmentStatus status);

	public List<Appointment> viewAllAppointmentsByIdAndDate(Integer id, Date date);

	boolean isFeedbackExistsForAppointment(int appointmentId);

	public List<Appointment> filterAppointmentsByVet(long vetId, AppointmentStatus status);

	public List<Appointment> filterAppointmentsbyPet(long petId, AppointmentStatus status);

	public List<Feedback> getAllFeedback();

	public List<Appointment> getAppointmentsByVetId(long vetId);

	public List<Appointment> getAppointmentsByPetParentId(long petparentId);

	public List<Appointment> getAppointmentsByPetId(long petId);

	public VetDto getVetById(long id);

	public PetParentDto getPetParentById(long id);

	Feedback submitFeedback(int appointmentId, Feedback feedback)
			throws FeedbackAlreadyExistsException, AppointmentNotFoundException, FeedbackSubmissionException;

}
