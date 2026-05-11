package org.example.projectjavawebapplication.repository;

import org.example.projectjavawebapplication.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository
        extends JpaRepository<Specialty, Long> {

}