package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.Specialty;

import java.util.List;

public interface SpecialtyService {

    List<Specialty> findAll();

    Specialty getById(Long id);

}