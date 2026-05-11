package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}