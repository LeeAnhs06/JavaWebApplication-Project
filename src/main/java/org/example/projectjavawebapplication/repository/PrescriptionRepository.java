package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription>
    findByStatus(
            String status
    );
}