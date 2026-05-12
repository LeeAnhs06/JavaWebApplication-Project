package org.example.projectjavawebapplication.service;

import org.example.projectjavawebapplication.entity.User;

import java.util.List;

public interface UserService {

    User login(String username, String password);

    User findByUsername(String username);

    void register(User user);

    void updateProfile(User user);

    User getById(Long id);

    List<User> getDoctors();
}