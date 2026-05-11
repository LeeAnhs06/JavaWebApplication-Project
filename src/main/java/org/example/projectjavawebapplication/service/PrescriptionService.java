package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.Prescription;
import org.example.projectjavawebapplication.entity.PrescriptionDetail;

import java.util.List;

public interface PrescriptionService {

    // prescription

    void save(
            Prescription prescription
    );

    Prescription getById(
            Long id
    );

    List<Prescription>
    getPendingPrescriptions();

    // detail

    void saveDetail(
            PrescriptionDetail detail
    );

    List<PrescriptionDetail>
    getDetails(
            Prescription prescription
    );
}