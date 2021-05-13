package com.example.demo;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner start(UserRepository userRepository) {
        return args -> {
            String password = passwordEncoder.encode("password");
            User user = new User(null, "user1@gmail.com", password);
            userRepository.save(user);
            User user1 = new User(null, "user2@gmail.com", password);
            userRepository.save(user1);
            User user2 = new User(null, "user3@gmail.com", password);
            userRepository.save(user2);

        };
    }

}
