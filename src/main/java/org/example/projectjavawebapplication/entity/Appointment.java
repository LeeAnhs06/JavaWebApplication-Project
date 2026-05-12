package org.example.projectjavawebapplication.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public Long getId() {
        return id;
    }

    public User getPatient() {
        return patient;
    }

    public User getDoctor() {
        return doctor;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}