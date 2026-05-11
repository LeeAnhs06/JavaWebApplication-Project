package org.example.projectjavawebapplication.controller;

import jakarta.servlet.http.HttpSession;
import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    // ================= HOME =================

    @GetMapping("/")
    public String home() {

        return "home";
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String loginPage() {

        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            String username,
            String password,
            HttpSession session,
            Model model
    ) {

        // validate username

        if (username == null ||
                username.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Username không được để trống"
            );

            return "auth/login";
        }

        // validate password

        if (password == null ||
                password.trim().isEmpty()) {

            model.addAttribute(
                    "error",
                    "Password không được để trống"
            );

            return "auth/login";
        }

        User user = userService.login(
                username.trim(),
                password.trim()
        );

        if (user != null) {

            session.setAttribute("user", user);

            // ADMIN

            if (user.getRole().equals("ADMIN")) {

                return "redirect:/admin/dashboard";
            }

            // DOCTOR

            if (user.getRole().equals("DOCTOR")) {

                return "redirect:/doctor";
            }

            // PATIENT

            return "redirect:/patient";
        }

        model.addAttribute(
                "error",
                "Sai tài khoản hoặc mật khẩu"
        );

        return "auth/login";
    }

    // ================= REGISTER =================

    @GetMapping("/register")
    public String registerPage() {

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            User user,
            @RequestParam("confirmPassword")
            String confirmPassword,
            Model model
    ) {

        // username empty

        if (user.getUsername() == null ||
                user.getUsername().trim().isEmpty()) {

            model.addAttribute(
                    "usernameError",
                    "Username không được để trống"
            );

            return "auth/register";
        }

        // username length

        if (user.getUsername().length() < 4) {

            model.addAttribute(
                    "usernameError",
                    "Username tối thiểu 4 ký tự"
            );

            return "auth/register";
        }

        // password empty

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            model.addAttribute(
                    "passwordError",
                    "Password không được để trống"
            );

            return "auth/register";
        }

        // password length

        if (user.getPassword().length() < 6) {

            model.addAttribute(
                    "passwordError",
                    "Password tối thiểu 6 ký tự"
            );

            return "auth/register";
        }

        // confirm password

        if (!user.getPassword().equals(confirmPassword)) {

            model.addAttribute(
                    "confirmError",
                    "Mật khẩu xác nhận không khớp"
            );

            return "auth/register";
        }

        // username existed

        User checkUser =
                userService.findByUsername(
                        user.getUsername()
                );

        if (checkUser != null) {

            model.addAttribute(
                    "usernameError",
                    "Username đã tồn tại"
            );

            return "auth/register";
        }

        // default role

        user.setRole("PATIENT");

        userService.register(user);

        model.addAttribute(
                "success",
                "Đăng ký thành công"
        );

        return "auth/login";
    }

    // ================= PATIENT =================

    @GetMapping("/patient")
    public String patientPage(
            HttpSession session
    ) {

        User user =
                (User) session.getAttribute("user");

        if (user == null ||
                !user.getRole().equals("PATIENT")) {

            return "redirect:/login";
        }

        return "patient/dashboard";
    }

    // ================= DOCTOR =================

    @GetMapping("/doctor")
    public String doctorPage(
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

    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(
            HttpSession session
    ) {

        session.invalidate();

        return "redirect:/login";
    }
}