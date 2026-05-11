package org.example.projectjavawebapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.example.projectjavawebapplication.entity.Appointment;
import org.example.projectjavawebapplication.entity.MedicalRecord;
import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.service.AppointmentService;
import org.example.projectjavawebapplication.service.MedicalRecordService;
import org.example.projectjavawebapplication.service.SpecialtyService;
import org.example.projectjavawebapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalRecordService medicalRecordService;

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        return "patient/dashboard";
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "patient/profile";
    }

    // ================= UPDATE PROFILE =================

    @PostMapping("/profile/update")
    public String updateProfile(User formUser, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        user.setFullName(formUser.getFullName());
        user.setEmail(formUser.getEmail());
        user.setBirthDate(formUser.getBirthDate());

        userService.updateProfile(user);

        session.setAttribute("user", user);

        return "redirect:/patient/profile";
    }

    // ================= APPOINTMENTS PAGE (BOOK + LIST) =================

    @GetMapping("/appointments")
    public String appointmentsPage(HttpSession session, Model model) {

        User patient = (User) session.getAttribute("user");

        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("appointments", appointmentService.findByPatient(patient));

        model.addAttribute("appointment", new Appointment());
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("doctors", userService.getDoctors());

        return "patient/book-appointments";
    }

    // giữ route cũ để không bị 404 nếu template còn link
    @GetMapping("/appointments/book")
    public String appointmentForm(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"PATIENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        return "redirect:/patient/appointments";
    }

    // ================= SAVE APPOINTMENT =================
    // ✅ validate required fields bằng backend (không dùng required HTML5)

    @PostMapping("/appointments/save")
    public String saveAppointment(
            Appointment appointment,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long specialtyId,
            HttpSession session,
            Model model
    ) {

        User patient = (User) session.getAttribute("user");

        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        // ===== validate required =====
        if (specialtyId == null) {
            model.addAttribute("error", "Vui lòng chọn chuyên khoa");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        if (doctorId == null) {
            model.addAttribute("error", "Vui lòng chọn bác sĩ");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        if (appointment.getAppointmentDate() == null) {
            model.addAttribute("error", "Vui lòng chọn ngày khám");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        if (appointment.getAppointmentTime() == null) {
            model.addAttribute("error", "Vui lòng chọn giờ khám");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        // ===== load specialty =====
        var specialty = specialtyService.getById(specialtyId);
        if (specialty == null) {
            model.addAttribute("error", "Chuyên khoa không hợp lệ");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }
        appointment.setSpecialty(specialty);

        // ===== load doctor =====
        User doctor = userService.getById(doctorId);
        if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
            model.addAttribute("error", "Bác sĩ không hợp lệ");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }
        appointment.setDoctor(doctor);

        // ===== validate date/time =====
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            model.addAttribute("error", "Không thể đặt lịch trong quá khứ");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        if (appointment.getAppointmentDate().isEqual(LocalDate.now())
                && appointment.getAppointmentTime().isBefore(LocalTime.now())) {
            model.addAttribute("error", "Không thể đặt giờ khám trong quá khứ");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        // ===== check duplicate slot =====
        boolean booked = appointmentService.isDoctorBooked(
                appointment.getDoctor(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        if (booked) {
            model.addAttribute("error", "Bác sĩ đã có lịch khám giờ này");
            return reloadAppointmentPageWithError(patient, appointment, model);
        }

        appointment.setPatient(patient);

        // ✅ vừa đặt lịch xong -> chưa thanh toán
        appointment.setStatus("UNPAID");

        appointmentService.save(appointment);

        // ✅ chuyển sang trang thanh toán
        return "redirect:/patient/payment/" + appointment.getId();
    }

    // ================= CANCEL APPOINTMENT (CORE-09) =================
    // ✅ chỉ cho hủy PENDING và phải trước 24h

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User patient = (User) session.getAttribute("user");
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return "redirect:/patient/appointments";
        }

        if (appointment.getPatient() == null ||
                !appointment.getPatient().getId().equals(patient.getId())) {
            return "redirect:/patient/appointments";
        }

        if (!"PENDING".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy lịch ở trạng thái PENDING");
            return "redirect:/patient/appointments";
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        long hours = Duration.between(LocalDateTime.now(), appointmentDateTime).toHours();

        if (hours < 24) {
            redirectAttributes.addFlashAttribute("error", "Chỉ được hủy trước giờ khám ít nhất 24 giờ");
            return "redirect:/patient/appointments";
        }

        appointment.setStatus("CANCELLED");
        appointmentService.save(appointment);

        redirectAttributes.addFlashAttribute("success", "Hủy lịch thành công");
        return "redirect:/patient/appointments";
    }

    // ================= PAYMENT PAGE =================
    // ✅ giá fix cứng 500000 + QR

    @GetMapping("/payment/{id}")
    public String paymentPage(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ) {
        User patient = (User) session.getAttribute("user");
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return "redirect:/patient/appointments";
        }

        if (appointment.getPatient() == null ||
                !appointment.getPatient().getId().equals(patient.getId())) {
            return "redirect:/patient/appointments";
        }

        int amount = 500000; // ✅ FIX CỨNG 500000

        String qrData = "PAY|" + appointment.getId() + "|" + amount;

        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=" +
                java.net.URLEncoder.encode(qrData, java.nio.charset.StandardCharsets.UTF_8);

        model.addAttribute("appointment", appointment);
        model.addAttribute("amount", amount);
        model.addAttribute("qrUrl", qrUrl);

        return "patient/payment";
    }

    // ================= CONFIRM PAYMENT =================

    @PostMapping("/payment/confirm/{id}")
    public String confirmPayment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        User patient = (User) session.getAttribute("user");
        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return "redirect:/patient/appointments";
        }

        if (appointment.getPatient() == null ||
                !appointment.getPatient().getId().equals(patient.getId())) {
            return "redirect:/patient/appointments";
        }

        if (!"UNPAID".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Lịch này không ở trạng thái chờ thanh toán");
            return "redirect:/patient/appointments";
        }

        // ✅ thanh toán xong -> chờ khám
        appointment.setStatus("PENDING");
        appointmentService.save(appointment);

        redirectAttributes.addFlashAttribute("success", "Thanh toán thành công. Bạn đã có thể chờ khám!");
        return "redirect:/patient/appointments";
    }

    // ================= HISTORY =================

    @GetMapping("/history")
    public String history(HttpSession session, Model model) {

        User patient = (User) session.getAttribute("user");

        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("records", medicalRecordService.getPatientHistory(patient));

        return "patient/history";
    }

    // ================= HISTORY DETAIL =================

    @GetMapping("/history/{id}")
    public String historyDetail(@PathVariable Long id, HttpSession session, Model model) {

        User patient = (User) session.getAttribute("user");

        if (patient == null || !"PATIENT".equals(patient.getRole())) {
            return "redirect:/login";
        }

        MedicalRecord medicalRecord = medicalRecordService.getById(id);

        model.addAttribute("record", medicalRecord);

        return "patient/history-detail";
    }

    // ================= HELPERS =================

    private String reloadAppointmentPageWithError(User patient, Appointment appointment, Model model) {

        model.addAttribute("appointments", appointmentService.findByPatient(patient));
        model.addAttribute("appointment", appointment);
        model.addAttribute("specialties", specialtyService.findAll());
        model.addAttribute("doctors", userService.getDoctors());

        return "patient/book-appointments";
    }
}