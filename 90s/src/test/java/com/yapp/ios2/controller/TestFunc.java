package com.yapp.ios2.controller;

import com.yapp.ios2.config.JwtProvider;
import com.yapp.ios2.repository.UserRepository;
import com.yapp.ios2.service.UserService;
import com.yapp.ios2.vo.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

public class TestFunc {

        public static String createTester(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider){
        User testUser = userRepository.findByEmail("tester@90s.com").orElse(
                User.builder()
                        .email("tester@90s.com")
                        .name("90s_tester")
                        .password(passwordEncoder.encode("test"))
                        .phone("010-0000-0000")
                        .roles(Collections.singletonList("ROLE_TESTER"))
                        .build()
        );
        if(!userRepository.findByEmail(testUser.getEmail()).isPresent()){
            userRepository.save(testUser);
        }
        String jwt = jwtProvider.createToken(testUser.getUid().toString(), testUser.getRoles());
        return jwt;
    }

    public static void deleteTester(String jwt, UserService userService, UserRepository userRepository, JwtProvider jwtProvider){
            User user = userRepository.findByEmail(jwtProvider.getUserName(jwt)).get();
            userService.delete(user);
    }


}
