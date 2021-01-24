package com.yapp.ios2.service;

import com.yapp.ios2.config.exception.UserNotFoundException;
import com.yapp.ios2.dto.UserDto;
import com.yapp.ios2.repository.AlbumOwnerRepository;
import com.yapp.ios2.repository.AlbumRepository;
import com.yapp.ios2.repository.UserRepository;
import com.yapp.ios2.vo.Album;
import com.yapp.ios2.vo.AlbumOwner;
import com.yapp.ios2.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    AlbumOwnerRepository albumOwnerRepository;
    @Autowired
    AlbumService albumService;

    private final PasswordEncoder passwordEncoder;


    public User join(String emailKakao, String emailApple, String emailGoogle, String name, String phone){

        User newUser = userRepository.findUserByPhone(phone).orElse(
                User.builder()
                        .roles(Collections.singletonList("ROLE_USER"))
                        .phoneNum(phone)
                        .build()
        );

        if(!emailKakao.isBlank()){
            newUser.setEmailKakao(emailKakao);
        }else if(!emailApple.isBlank()){
            newUser.setEmailApple(emailApple);
        }else if(!emailGoogle.isBlank()) {
            newUser.setEmailGoogle(emailGoogle);
        }

        userRepository.save(newUser);
        return newUser;
    }

    public User login(String emailKakao, String emailApple, String emailGoogle){

        User user = new User();

        if(!emailKakao.isBlank()){
            user = userRepository.findUserByEmailKakao(emailKakao)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        }else if(!emailApple.isBlank()){
            user = userRepository.findUserByEmailApple(emailApple)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        }else if(!emailGoogle.isBlank()) {
            user = userRepository.findUserByEmailGoogle(emailGoogle)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        }
        return user;
    }

    public void delete(User user){

        List<Album> albums = albumRepository.findByUser(user);
        System.out.println("SIZE OF ALBUMS : " + albums.size());
        for(Album album : albums){

            // 엘범 소유자가 한명이상이며, 삭제하려는 유저가 CREATOR인 경우 역할을 바꿔줘야 함.
            if(albumOwnerRepository.findByAlbumUid(album.getUid()).size() > 1) {
//                엘범소유자가 한명 이상일때
                if(albumOwnerRepository.findByAlbumAndUser(album, user).getRole().contains("CREATOR")){
                    // 삭제 유저가 CREATOR인 경우
                    AlbumOwner newCreator = albumOwnerRepository.findByAlbumUidandRole(album.getUid(), "GUEST").get(0);
                    newCreator.setRole("ROLE_CREATOR");
                    albumOwnerRepository.save(newCreator);
                }
            }else{
                albumService.removeAlbum(album);
            }

        }

        userRepository.delete(user);

    }

    public User getUserByPhone(String phone){
        return userRepository.findUserByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL"));
    }

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return userRepository.findById(Long.parseLong(uid))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    public User findByPhone(String phoneNumber){
        User user = userRepository.findUserByPhone(phoneNumber).orElseThrow(
                () -> new UserNotFoundException()
        );

        return user;

    }

    public User updatePhoneNumber(User user, String phoneNumber){
        user.setPhoneNum(phoneNumber);
        userRepository.save(user);
        return user;
    }

    public User updatePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

}
