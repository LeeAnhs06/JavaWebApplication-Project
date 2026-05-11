package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.Specialty;
import org.example.projectjavawebapplication.repository.SpecialtyRepository;
import org.example.projectjavawebapplication.service.SpecialtyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl
        implements SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Override
    public List<Specialty> findAll() {

        return specialtyRepository.findAll();
    }

    @Override
    public Specialty getById(Long id) {
        return specialtyRepository.findById(id).orElse(null);
    }
}