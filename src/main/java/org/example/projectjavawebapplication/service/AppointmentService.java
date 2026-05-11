package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.Appointment;
import org.example.projectjavawebapplication.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    void save(Appointment appointment);

    boolean isDoctorBooked(
            User doctor,
            LocalDate date,
            LocalTime time
    );

    List<Appointment> getPatientAppointments(User patient);

    List<Appointment> getDoctorAppointments(User doctor);

    List<Appointment> getPendingAppointments();

    List<Appointment> findByPatient(User patient);

    List<Appointment> findPendingAppointments();

    Appointment getById(Long id);

    List<Appointment> findPendingAppointmentsByDoctor(User doctor);
}