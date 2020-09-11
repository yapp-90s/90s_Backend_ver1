package com.yapp.ios2.controller;

import com.yapp.ios2.config.JwtProvider;
import com.yapp.ios2.repository.UserRepository;
import com.yapp.ios2.service.AlbumService;
import com.yapp.ios2.service.UserService;
import com.yapp.ios2.vo.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;

public class TestFunc {

    public static User createTester(UserRepository userRepository, PasswordEncoder passwordEncoder){
        User testUser = userRepository.findUserByPhone("010-9523-3114").orElse(
                User.builder()
                        .emailKakao("tester@90s.com")
                        .name("90s_tester")
                        .password(passwordEncoder.encode("test"))
                        .phone("010-9523-3114")
                        .roles(Collections.singletonList("ROLE_TESTER"))
                        .build()
        );
        userRepository.save(testUser);
        return testUser;
    }

    public static void deleteTester(String jwt, UserService userService, UserRepository userRepository, JwtProvider jwtProvider){
            User user = userRepository.findUserByPhone(jwtProvider.getUserName(jwt)).get();
            userService.delete(user);
    }
    public static void deleteTester(UserService userService, UserRepository userRepository, JwtProvider jwtProvider){
        User user = userRepository.findUserByPhone("010-9523-3114").get();
        userService.delete(user);
    }

    public static void createAlbums(Integer tot, User user, AlbumService albumService){
        for(int i = 1; i <= tot; i++){
            albumService.create(
                    "NameOfAlbum " + tot,
                    tot + 1,
                    user.getUid(),
                    1L,
                    LocalDate.of(LocalDate.now().getYear()+tot, LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth())
            );
        }
    }


}
