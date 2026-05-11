package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.MedicalRecord;
import org.example.projectjavawebapplication.entity.User;

import java.util.List;

public interface MedicalRecordService {

    void save(MedicalRecord medicalRecord);

    List<MedicalRecord> getPatientHistory(
            User patient
    );

    MedicalRecord getById(Long id);
}