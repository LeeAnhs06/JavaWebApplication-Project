package org.example.projectjavawebapplication.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "prescription_details")
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private Integer quantity;

    public Long getId() {
        return id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}