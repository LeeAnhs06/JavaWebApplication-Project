package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.Appointment;
import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.repository.AppointmentRepository;
import org.example.projectjavawebapplication.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl
        implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void save(Appointment appointment) {

        appointmentRepository.save(appointment);
    }

@Override
public boolean isDoctorBooked(
        User doctor,
        LocalDate date,
        LocalTime time
) {

    return appointmentRepository
            .existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
                    doctor,
                    date,
                    time,
                    "CANCELLED"
            );
}

    @Override
    public List<Appointment> getPatientAppointments(
            User patient
    ) {

        return appointmentRepository
                .findByPatient(patient);
    }

    @Override
    public List<Appointment> getDoctorAppointments(
            User doctor
    ) {

        return appointmentRepository
                .findByDoctor(doctor);
    }

    @Override
    public List<Appointment> getPendingAppointments() {

        return appointmentRepository
                .findByStatus("PENDING");
    }

    @Override
public List<Appointment> findByPatient(User patient) {

    return appointmentRepository
            .findByPatient(patient);
}
@Override
public List<Appointment> findPendingAppointments() {

    return appointmentRepository.findByStatus("PENDING");
}
@Override
public Appointment getById(Long id) {

    return appointmentRepository
            .findById(id)
            .orElse(null);
}
@Override
public List<Appointment> findPendingAppointmentsByDoctor(User doctor) {
    return appointmentRepository.findByDoctorAndStatus(doctor, "PENDING");
}

}