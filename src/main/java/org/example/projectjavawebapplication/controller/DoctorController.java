package org.example.projectjavawebapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.example.projectjavawebapplication.entity.*;
import org.example.projectjavawebapplication.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private MedicineService medicineService;

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String doctorDashboard(
            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("user");

        if (user == null ||
                !user.getRole().equals("DOCTOR")) {

            return "redirect:/login";
        }

        return "doctor/dashboard";
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public String doctorProfile(
            HttpSession session,
            Model model
    ) {

        User user =
                (User) session.getAttribute("user");

        if (user == null ||
                !user.getRole().equals("DOCTOR")) {

            return "redirect:/login";
        }

        model.addAttribute(
                "user",
                user
        );

        return "doctor/profile";
    }

    // ================= UPDATE PROFILE =================

    @PostMapping("/profile/update")
    public String updateProfile(
            User formUser,
            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("user");

        if (user == null) {

            return "redirect:/login";
        }

        user.setFullName(
                formUser.getFullName()
        );

        user.setEmail(
                formUser.getEmail()
        );

        user.setBirthDate(
                formUser.getBirthDate()
        );

        userService.updateProfile(user);

        session.setAttribute(
                "user",
                user
        );

        return "redirect:/doctor/profile";
    }

    // ================= APPOINTMENTS =================

@GetMapping("/appointments")
public String appointments(HttpSession session, Model model) {

    User user = (User) session.getAttribute("user");

    if (user == null || !user.getRole().equals("DOCTOR")) {
        return "redirect:/login";
    }

    model.addAttribute(
            "appointments",
            appointmentService.findPendingAppointmentsByDoctor(user) // thêm method này
    );

    return "doctor/appointments";
}

    // ================= EXAM FORM =================

    @GetMapping("/examine/{id}")
    public String examineForm(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("user");

        if (user == null ||
                !user.getRole().equals("DOCTOR")) {

            return "redirect:/login";
        }

        model.addAttribute(
                "appointment",
                appointmentService.getById(id)
        );

        model.addAttribute(
                "medicines",
                medicineService.getAll()
        );

        return "doctor/examine";
    }

    // ================= SAVE EXAM =================

@PostMapping("/examine/save")
public String saveExam(
        @RequestParam Long appointmentId,
        @RequestParam(required = false) String symptoms,
        @RequestParam(required = false) String diagnosis,
        @RequestParam(value = "medicineIds", required = false) Long[] medicineIds,
        @RequestParam(value = "quantities", required = false) Integer[] quantities,
        HttpSession session,
        Model model
) {

    User doctor = (User) session.getAttribute("user");
    if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
        return "redirect:/login";
    }

    Appointment appointment = appointmentService.getById(appointmentId);
    if (appointment == null) {
        return "redirect:/doctor/appointments";
    }

    // chỉ khám lịch của chính mình
    if (appointment.getDoctor() == null ||
            !appointment.getDoctor().getId().equals(doctor.getId())) {
        return "redirect:/doctor/appointments";
    }

    // chỉ khám khi còn PENDING
    if (!"PENDING".equals(appointment.getStatus())) {
        return "redirect:/doctor/appointments";
    }

    // ===== VALIDATE REQUIRED FIELDS =====
    if (symptoms == null || symptoms.trim().isEmpty()) {
        model.addAttribute("error", "Triệu chứng không được để trống");
        model.addAttribute("appointment", appointment);
        model.addAttribute("medicines", medicineService.getAll());
        return "doctor/examine";
    }

    if (diagnosis == null || diagnosis.trim().isEmpty()) {
        model.addAttribute("error", "Chẩn đoán không được để trống");
        model.addAttribute("appointment", appointment);
        model.addAttribute("medicines", medicineService.getAll());
        return "doctor/examine";
    }

    // Nếu có chọn thuốc thì quantity phải hợp lệ (>0)
    if (medicineIds != null) {
        if (quantities == null || quantities.length < medicineIds.length) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ số lượng cho các thuốc đã chọn");
            model.addAttribute("appointment", appointment);
            model.addAttribute("medicines", medicineService.getAll());
            return "doctor/examine";
        }

        for (int i = 0; i < medicineIds.length; i++) {
            if (medicineIds[i] == null) {
                model.addAttribute("error", "Thuốc không hợp lệ");
                model.addAttribute("appointment", appointment);
                model.addAttribute("medicines", medicineService.getAll());
                return "doctor/examine";
            }

            Integer q = quantities[i];
            if (q == null || q <= 0) {
                model.addAttribute("error", "Số lượng thuốc phải > 0");
                model.addAttribute("appointment", appointment);
                model.addAttribute("medicines", medicineService.getAll());
                return "doctor/examine";
            }
        }
    }

    // ===== MEDICAL RECORD =====
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setAppointment(appointment);
    medicalRecord.setSymptoms(symptoms.trim());
    medicalRecord.setDiagnosis(diagnosis.trim());
    medicalRecordService.save(medicalRecord);

    // ===== PRESCRIPTION =====
    Prescription prescription = new Prescription();
    prescription.setMedicalRecord(medicalRecord);
    prescriptionService.save(prescription);

    // ===== DETAILS =====
    if (medicineIds != null) {
        for (int i = 0; i < medicineIds.length; i++) {
            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setPrescription(prescription);
            detail.setMedicine(medicineService.getById(medicineIds[i]));
            detail.setQuantity(quantities[i]);
            prescriptionService.saveDetail(detail);
        }
    }

    appointment.setStatus("COMPLETED");
    appointmentService.save(appointment);

    return "redirect:/doctor/appointments";
}
    @GetMapping("/prescriptions")
public String prescriptions(
        HttpSession session,
        Model model
) {

    User user =
            (User) session.getAttribute("user");

    if (user == null ||
            !user.getRole().equals("DOCTOR")) {

        return "redirect:/login";
    }

    model.addAttribute(
            "prescriptions",
            prescriptionService
                    .getPendingPrescriptions()
    );

    return "doctor/prescriptions";
}

@PostMapping("/prescriptions/dispense/{id}")
public String dispenseMedicine(
        @PathVariable Long id
) {

    Prescription prescription =
            prescriptionService.getById(id);

    // lấy danh sách thuốc

    for (PrescriptionDetail detail :
            prescriptionService.getDetails(
                    prescription
            )) {

        Medicine medicine =
                detail.getMedicine();

        // trừ tồn kho

        medicine.setStock(
                medicine.getStock()
                        - detail.getQuantity()
        );

        medicineService.save(
                medicine
        );
    }

    // đổi trạng thái

    prescription.setStatus(
            "DISPENSED"
    );

    prescriptionService.save(
            prescription
    );

    return "redirect:/doctor/prescriptions";
}
}