package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Prescription;
import org.example.projectjavawebapplication.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionDetailRepository
        extends JpaRepository<PrescriptionDetail, Long> {

    List<PrescriptionDetail>
    findByPrescription(
            Prescription prescription
    );
}