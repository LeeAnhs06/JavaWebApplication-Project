package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.MedicalRecord;
import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.repository.MedicalRecordRepository;
import org.example.projectjavawebapplication.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordServiceImpl
        implements MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Override
    public void save(
            MedicalRecord medicalRecord
    ) {

        medicalRecordRepository.save(
                medicalRecord
        );
    }

    @Override
    public List<MedicalRecord> getPatientHistory(
            User patient
    ) {

        return medicalRecordRepository
                .findByAppointmentPatient(
                        patient
                );
    }

    @Override
    public MedicalRecord getById(
            Long id
    ) {

        return medicalRecordRepository
                .findById(id)
                .orElse(null);
    }
}