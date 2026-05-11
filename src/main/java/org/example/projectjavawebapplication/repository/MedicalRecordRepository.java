package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.MedicalRecord;
import org.example.projectjavawebapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByAppointmentPatient(
            User patient
    );
}