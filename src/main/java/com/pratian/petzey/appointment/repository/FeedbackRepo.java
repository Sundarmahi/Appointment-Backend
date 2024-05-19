package com.pratian.petzey.appointment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
 
import com.pratian.petzey.appointment.entities.Feedback;
 
@Repository
public interface FeedbackRepo extends JpaRepository<Feedback,Integer> {
	

	 boolean existsByAppointment_AppointmentId(int appointmentId);
    
	 
}
 