package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.Medicine;

import java.util.List;

public interface MedicineService {

    List<Medicine> getAll();

    void save(Medicine medicine);

    void delete(Long id);

    Medicine getById(Long id);
}