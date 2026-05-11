package org.example.projectjavawebapplication;

import org.example.projectjavawebapplication.entity.User;
import org.example.projectjavawebapplication.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProjectJavaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectJavaWebApplication.class, args);
    }


    @Bean
    CommandLineRunner init(UserRepository userRepository){

    return args -> {

        if(userRepository.findByUsername("admin")==null){

            User admin = new User();

            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setRole("ADMIN");

            userRepository.save(admin);
        }

    };
}
}
