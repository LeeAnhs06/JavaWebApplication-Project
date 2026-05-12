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

    // dashboard

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

    // profile

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

    // update profile

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

    // appointments

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

    // examine form

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

    // luu form

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

        // validate form
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

        // lịch sử
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        medicalRecord.setSymptoms(symptoms.trim());
        medicalRecord.setDiagnosis(diagnosis.trim());
        medicalRecordService.save(medicalRecord);

        // đơn thuốc
        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(medicalRecord);
        prescriptionService.save(prescription);

        // chi tiết
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
        @PathVariable Long id,
        HttpSession session,
        Model model
) {
    User user = (User) session.getAttribute("user");
    if (user == null || !"DOCTOR".equals(user.getRole())) {
        return "redirect:/login";
    }

    Prescription prescription = prescriptionService.getById(id);
    if (prescription == null) {
        return "redirect:/doctor/prescriptions";
    }

    // chỉ cấp phát khi còn PENDING (tránh bấm 2 lần)
    if (!"PENDING".equals(prescription.getStatus())) {
        return "redirect:/doctor/prescriptions";
    }

    var details = prescriptionService.getDetails(prescription);

    // validate: phải có chi tiết thuốc
    if (details == null || details.isEmpty()) {
        model.addAttribute("error", "Đơn thuốc không có thuốc để cấp phát");
        model.addAttribute("prescriptions", prescriptionService.getPendingPrescriptions());
        return "doctor/prescriptions";
    }

    // 1) CHECK STOCK trước
    for (PrescriptionDetail detail : details) {
        Medicine medicine = detail.getMedicine();
        if (medicine == null) {
            model.addAttribute("error", "Thuốc không hợp lệ");
            model.addAttribute("prescriptions", prescriptionService.getPendingPrescriptions());
            return "doctor/prescriptions";
        }
        if (medicine.getStock() < detail.getQuantity()) {
            model.addAttribute(
                    "error",
                    "Thuốc " + medicine.getName() + " không đủ số lượng"
            );
            model.addAttribute("prescriptions", prescriptionService.getPendingPrescriptions());
            return "doctor/prescriptions";
        }
    }

    // 2) TRỪ STOCK sau khi đã check ok hết
    for (PrescriptionDetail detail : details) {
        Medicine medicine = detail.getMedicine();
        medicine.setStock(medicine.getStock() - detail.getQuantity());
        medicineService.save(medicine);
    }

    // đổi trạng thái
    prescription.setStatus("DISPENSED");
    prescriptionService.save(prescription);

    return "redirect:/doctor/prescriptions";
}
}