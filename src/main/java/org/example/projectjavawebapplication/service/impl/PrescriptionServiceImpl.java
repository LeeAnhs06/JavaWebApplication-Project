package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.Prescription;
import org.example.projectjavawebapplication.entity.PrescriptionDetail;
import org.example.projectjavawebapplication.repository.PrescriptionDetailRepository;
import org.example.projectjavawebapplication.repository.PrescriptionRepository;
import org.example.projectjavawebapplication.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionServiceImpl
        implements PrescriptionService {

    @Autowired
    private PrescriptionRepository
            prescriptionRepository;

    @Autowired
    private PrescriptionDetailRepository
            detailRepository;

    // ================= SAVE PRESCRIPTION =================

    @Override
    public void save(
            Prescription prescription
    ) {

        prescriptionRepository.save(
                prescription
        );
    }

    // ================= SAVE DETAIL =================

    @Override
    public void saveDetail(
            PrescriptionDetail detail
    ) {

        detailRepository.save(
                detail
        );
    }

    // ================= GET BY ID =================

    @Override
    public Prescription getById(
            Long id
    ) {

        return prescriptionRepository
                .findById(id)
                .orElse(null);
    }

    // ================= PENDING PRESCRIPTIONS =================

    @Override
    public List<Prescription>
    getPendingPrescriptions() {

        return prescriptionRepository
                .findByStatus("PENDING");
    }

    // ================= DETAILS =================

    @Override
    public List<PrescriptionDetail>
    getDetails(
            Prescription prescription
    ) {

        return detailRepository
                .findByPrescription(
                        prescription
                );
    }
}