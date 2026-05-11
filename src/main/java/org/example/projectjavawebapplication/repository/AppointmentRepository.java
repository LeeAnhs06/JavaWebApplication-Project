package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Appointment;
import org.example.projectjavawebapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // ================= CHECK DUPLICATE =================

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
            User doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String status
    );

    // ================= PATIENT =================

    List<Appointment> findByPatient(
            User patient
    );

    // ================= DOCTOR =================

    List<Appointment> findByDoctor(
            User doctor
    );

    // ================= STATUS =================

    List<Appointment> findByStatus(
            String status
    );
    List<Appointment> findByDoctorAndStatus(User doctor, String status);
}