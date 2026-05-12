package org.example.projectjavawebapplication.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;

    @OneToMany(mappedBy = "prescription")
    private List<PrescriptionDetail> details;


    private String status = "PENDING";


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(
            MedicalRecord medicalRecord
    ) {
        this.medicalRecord = medicalRecord;
    }

    public List<PrescriptionDetail> getDetails() {
        return details;
    }

    public void setDetails(
            List<PrescriptionDetail> details
    ) {
        this.details = details;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }
}