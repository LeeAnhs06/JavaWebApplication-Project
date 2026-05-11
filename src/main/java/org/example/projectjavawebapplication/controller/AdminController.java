package org.example.projectjavawebapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.example.projectjavawebapplication.entity.Medicine;
import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.service.MedicineService;
import org.example.projectjavawebapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.example.projectjavawebapplication.entity.Prescription;
import org.example.projectjavawebapplication.entity.PrescriptionDetail;
import org.example.projectjavawebapplication.service.PrescriptionService;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    private MedicineService medicineService;

    @Autowired
    private UserService userService;

    @Autowired
private PrescriptionService prescriptionService;

    // ================= CHECK ADMIN =================

    private boolean isAdmin(HttpSession session) {

        User user =
                (User) session.getAttribute("user");

        return user != null &&
                user.getRole().equals("ADMIN");
    }

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        return "admin/dashboard";
    }

    // ================= PROFILE =================

    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        User sessionUser =
                (User) session.getAttribute("user");

        User user =
                userService.getById(sessionUser.getId());

        model.addAttribute("user", user);

        return "admin/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            User formUser,
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        User sessionUser =
                (User) session.getAttribute("user");

        User user =
                userService.getById(sessionUser.getId());

        user.setFullName(formUser.getFullName());

        user.setEmail(formUser.getEmail());

        user.setBirthDate(formUser.getBirthDate());

        // validation

        if (user.getFullName() == null ||
                user.getFullName().trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Họ tên không được để trống"
            );

            model.addAttribute("user", user);

            return "admin/profile";
        }

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Email không được để trống"
            );

            model.addAttribute("user", user);

            return "admin/profile";
        }

        userService.updateProfile(user);

        model.addAttribute(
                "success",
                "Cập nhật thành công"
        );

        model.addAttribute("user", user);

        return "admin/profile";
    }

    // ================= MEDICINE LIST =================

    @GetMapping("/medicines")
    public String medicines(
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "list",
                medicineService.getAll()
        );

        return "admin/medicine/medicine-list";
    }

    // ================= ADD FORM =================

    @GetMapping("/medicines/add")
    public String addForm(
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "medicine",
                new Medicine()
        );

        return "admin/medicine/medicine-form";
    }

    // ================= SAVE =================

    @PostMapping("/medicines/save")
    public String saveMedicine(
            Medicine m,
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // validate name

        if (m.getName() == null ||
                m.getName().trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Tên thuốc không được để trống"
            );

            model.addAttribute("medicine", m);

            return "admin/medicine/medicine-form";
        }

        // validate stock

        if (m.getStock() < 0) {

            model.addAttribute(
                    "error",
                    "Stock không được âm"
            );

            model.addAttribute("medicine", m);

            return "admin/medicine/medicine-form";
        }

        // validate price

        if (m.getPrice() < 0) {

            model.addAttribute(
                    "error",
                    "Price không được âm"
            );

            model.addAttribute("medicine", m);

            return "admin/medicine/medicine-form";
        }

        medicineService.save(m);

        return "redirect:/admin/medicines";
    }

    // ================= EDIT =================

    @GetMapping("/medicines/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            HttpSession session,
            Model model
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Medicine medicine =
                medicineService.getById(id);

        model.addAttribute(
                "medicine",
                medicine
        );

        return "admin/medicine/medicine-form";
    }

    // ================= DELETE =================

    @GetMapping("/medicines/delete/{id}")
    public String deleteMedicine(
            @PathVariable Long id,
            HttpSession session
    ) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        medicineService.delete(id);

        return "redirect:/admin/medicines";
    }
    // ================= TEST TYPES (READ-ONLY) =================
@GetMapping("/test-types")
public String testTypes(HttpSession session, Model model) {

    if (!isAdmin(session)) {
        return "redirect:/login";
    }

    // Hardcode list để "làm màu" (CORE-04)
    List<String> testTypes = List.of(
            "Xét nghiệm máu tổng quát",
            "Xét nghiệm đường huyết",
            "Xét nghiệm mỡ máu",
            "Xét nghiệm chức năng gan",
            "Xét nghiệm chức năng thận",
            "Xét nghiệm nước tiểu",
            "Xét nghiệm viêm gan B",
            "Xét nghiệm viêm gan C"
    );

    model.addAttribute("testTypes", testTypes);

    return "admin/test-types";
}
// ================= PRESCRIPTIONS =================

@GetMapping("/prescriptions")
public String prescriptions(
        HttpSession session,
        Model model
) {

    if (!isAdmin(session)) {
        return "redirect:/login";
    }

    model.addAttribute(
            "prescriptions",
            prescriptionService.getPendingPrescriptions()
    );

    return "admin/prescriptions";
}

// ================= DISPENSE MEDICINE =================

@PostMapping("/prescriptions/dispense/{id}")
public String dispenseMedicine(
        @PathVariable Long id,
        HttpSession session,
        Model model
) {

    if (!isAdmin(session)) {
        return "redirect:/login";
    }

    Prescription prescription =
            prescriptionService.getById(id);

    if (prescription == null) {

        return "redirect:/admin/prescriptions";
    }

    // CHECK STOCK

    for (PrescriptionDetail detail
            : prescription.getDetails()) {

        Medicine medicine =
                detail.getMedicine();

        if (medicine.getStock()
                < detail.getQuantity()) {

            model.addAttribute(
                    "error",
                    "Thuốc "
                            + medicine.getName()
                            + " không đủ số lượng"
            );

            model.addAttribute(
                    "prescriptions",
                    prescriptionService.getPendingPrescriptions()
            );

            return "admin/prescriptions";
        }
    }

    // SUBTRACT STOCK

    for (PrescriptionDetail detail
            : prescription.getDetails()) {

        Medicine medicine =
                detail.getMedicine();

        medicine.setStock(
                medicine.getStock()
                        - detail.getQuantity()
        );

        medicineService.save(medicine);
    }

    // UPDATE STATUS

    prescription.setStatus(
            "DISPENSED"
    );

    prescriptionService.save(
            prescription
    );

    return "redirect:/admin/prescriptions";
}
}