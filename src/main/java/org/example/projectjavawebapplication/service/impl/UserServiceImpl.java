package org.example.projectjavawebapplication.service.impl;

import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.repository.UserRepository;
import org.example.projectjavawebapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // ================= LOGIN =================
    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // ================= REGISTER (HASH PASSWORD) =================
    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // ================= UPDATE PROFILE (DO NOT TOUCH PASSWORD) =================
    @Override
    public void updateProfile(User user) {
        // tuyệt đối không encode lại password ở đây
        userRepository.save(user);
    }

    // ================= FIND USERNAME =================
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ================= GET BY ID =================
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // ================= GET DOCTORS =================
    @Override
    public List<User> getDoctors() {
        return userRepository.findByRole("DOCTOR");
    }
}