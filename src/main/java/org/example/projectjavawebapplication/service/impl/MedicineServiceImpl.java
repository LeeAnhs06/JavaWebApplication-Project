package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.Medicine;
import org.example.projectjavawebapplication.repository.MedicineRepository;
import org.example.projectjavawebapplication.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineServiceImpl
        implements MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public List<Medicine> getAll() {

        return medicineRepository.findAll();
    }

    @Override
    public void save(Medicine medicine) {

        medicineRepository.save(medicine);
    }

    @Override
    public void delete(Long id) {

        medicineRepository.deleteById(id);
    }

    @Override
    public Medicine getById(Long id) {

        return medicineRepository
                .findById(id)
                .orElse(null);
    }


}