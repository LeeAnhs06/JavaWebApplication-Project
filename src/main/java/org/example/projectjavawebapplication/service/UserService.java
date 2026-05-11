package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.User;

import java.util.List;

public interface UserService {

    User login(String username, String password);

    User findByUsername(String username);

    // CORE-01: chỉ dùng cho đăng ký (hash password)
    void register(User user);

    // CORE-03: update profile không đụng password
    void updateProfile(User user);

    User getById(Long id);

    List<User> getDoctors();
}