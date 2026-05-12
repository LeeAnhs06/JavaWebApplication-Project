package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Appointment;
import org.example.projectjavawebapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // kiểm tra trùng

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
            User doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String status
    );

    // bệnh nhân

    List<Appointment> findByPatient(
            User patient
    );

    // bác sĩ

    List<Appointment> findByDoctor(
            User doctor
    );

    // trạng thái

    List<Appointment> findByStatus(
            String status
    );

    List<Appointment> findByDoctorAndStatus(User doctor, String status);
}