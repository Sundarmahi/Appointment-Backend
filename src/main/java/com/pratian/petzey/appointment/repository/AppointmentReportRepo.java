package com.pratian.petzey.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pratian.petzey.appointment.entities.AppointmentReport;

public interface AppointmentReportRepo extends JpaRepository<AppointmentReport, Integer>{

}
