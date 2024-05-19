package com.pratian.petzey.appointment.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pratian.petzey.appointment.entities.Appointment;
import com.pratian.petzey.appointment.entities.AppointmentStatus;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Integer> {

//	@Query(value = "SELECT COUNT(a) FROM Appointment a WHERE a.appointmentStatus = 'CONFIRMED' AND (a.vetId = :id OR a.petParentId = :id)")
//	int countConfirmedAppointments(@Param("id") int id);
//
//	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentStatus = 'CLOSED' AND (a.vetId = :id OR a.petParentId = :id)")
//	int countClosedAppointments(@Param("id") int id);
//
//	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentStatus = 'CANCELLED' AND (a.vetId = :id OR a.petParentId = :id)")
//	int countCancelledAppointments(@Param("id") int id);
//
//	@Query("SELECT COUNT(a) FROM Appointment a WHERE a.vetId = :id OR a.petParentId = :id")
//	int countTotalAppointments(@Param("id") int id);

	@Query("SELECT "
			+ "COALESCE(SUM(CASE WHEN a.appointmentStatus = 'CONFIRMED' THEN 1 ELSE 0 END),0) AS Confirmed, "
			+ "COALESCE(SUM(CASE WHEN a.appointmentStatus = 'CLOSED' THEN 1 ELSE 0 END),0) AS Closed, "
			+ "COALESCE(SUM(CASE WHEN a.appointmentStatus = 'CANCELLED' THEN 1 ELSE 0 END),0) AS Cancelled, "
			+ "COUNT(a) AS Total " + "FROM Appointment a "
			+ "WHERE a.vetId = :id OR a.petParentId=:id")
	public Map<String, Integer> getSummaryCounts(@Param("id") long id);
	
	@Query(value = "Select a from Appointment a where a.appointmentStatus = :status")
	public List<Appointment> findByStatus(@Param(value = "status") AppointmentStatus status);

	@Query(value = "Select a from Appointment a where a.appointmentId = :id AND a.appointmentDate =:date ")
	public List<Appointment> findByIdAndDate(@Param(value = "id") Integer id, @Param(value = "date") Date date);

	@Query("SELECT a FROM Appointment a WHERE a.vetId = :vetId ORDER BY a.appointmentDate,a.appointment_time")
	List<Appointment> findByVetId(@Param(value ="vetId") long vetId);

	@Query("SELECT a FROM Appointment a WHERE a.petParentId = :petParentId ORDER BY a.appointmentDate,a.appointment_time")
	List<Appointment> findByPetparentId(@Param(value ="petParentId") long petParentId);
	
	@Query("SELECT a FROM Appointment a WHERE a.petId = :petId ORDER BY a.appointmentDate,a.appointment_time")
	List<Appointment> findByPetId(@Param(value ="petId") long petId);
	
	@Query(value = "SELECT a FROM Appointment a WHERE a.vetId = :vetId AND a.appointmentStatus = :status ORDER BY a.appointmentDate,a.appointment_time")
	public List<Appointment> findByStatusByVet(@Param("vetId") long vetId, @Param("status") AppointmentStatus status);

	@Query(value = "SELECT a FROM Appointment a WHERE a.petParentId = :petId AND a.appointmentStatus = :status")
	public List<Appointment> findByStatusByPet(@Param("petId") long petId, @Param("status") AppointmentStatus status);


//	@Query("SELECT v, pp, p, ar, a FROM Appointment a " + "INNER JOIN a.petParent pp " + "INNER JOIN a.pet p "
//			+ "INNER JOIN a.vet v " + "INNER JOIN a.appointmentReport ar " + "WHERE a.appointmentId = :id")
//	public List<Object[]> getAppointmentDetailsById(@Param(value = "id") int id);
	
	@Query("SELECT a FROM Appointment a WHERE a.vetId = :vetId AND a.appointmentDate =   :date")
	public List<Appointment> checkVets(@Param(value ="vetId") long vetId, @Param (value = "date") java.sql.Date date);

}
