package com.yapp.ios2.service;

import com.yapp.ios2.config.exception.UserNotFoundException;
import com.yapp.ios2.dto.UserDto;
import com.yapp.ios2.repository.AlbumOwnerRepository;
import com.yapp.ios2.repository.AlbumRepository;
import com.yapp.ios2.repository.NoticeAgreementRepository;
import com.yapp.ios2.repository.UserRepository;
import com.yapp.ios2.vo.Album;
import com.yapp.ios2.vo.AlbumOwner;
import com.yapp.ios2.vo.NoticeAgreement;
import com.yapp.ios2.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    NoticeAgreementRepository noticeAgreementRepository;
    private final PasswordEncoder passwordEncoder;

//    일반 회원가입
    public User join(String email, String name, String password, String phone){
        User newUser = null;
        NoticeAgreement newNoticeAgreement;
        if(!checkEmail(email)){
            newUser = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .phone(phone)
                    .sosial(false)
                    .build();

            newNoticeAgreement = NoticeAgreement.builder()
                    .build();

            noticeAgreementRepository.save(newNoticeAgreement);

            userRepository.save(newUser);
        }else{
            throw new IllegalArgumentException("Existing Email");
        }
        return newUser;
    }

//    카카오 회원가입
    public User join(String email, String name, String phone){
        User newUser = null;
        if(!checkEmail(email)){
            newUser = User.builder()
                    .email(email)
                    .name(name)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .phone(phone)
                    .sosial(true)
                    .build();

            userRepository.save(newUser);
        }else{
            throw new IllegalArgumentException("Existing Email");
        }
        return newUser;
    }

    public User login(String email, String password){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
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

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("가입되지 않은 E-MAIL"));
    }

    public boolean checkEmail(String email){
        return userRepository.findByEmail(email)
                .isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return userRepository.findById(Long.parseLong(uid))
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//        return userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public User updateEmail(User user, String email){
        user.setEmail(email);
        userRepository.save(user);
        return user;
    }

    public User findByPhone(String phoneNumber){
        User user = userRepository.findUserByPhone(phoneNumber).orElseThrow(
                () -> new UserNotFoundException()
        );

        return user;

    }

    public User updatePhoneNumber(User user, String phoneNumber){
        user.setPhone(phoneNumber);
        userRepository.save(user);
        return user;
    }

    public User updatePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    public NoticeAgreement getNoticeAgreement(User user){
        return noticeAgreementRepository.findByUser(user);
    }

    public NoticeAgreement updateAgreement(User user, UserDto.NoticeAgreement noticeAgreement){

        NoticeAgreement noticeAgreementVo = noticeAgreementRepository.findByUser(user);

        if(noticeAgreementVo.getAlbumEndNotice()^noticeAgreement.getAlbumEndNotice()){
            noticeAgreementVo.setAlbumEndNotice(noticeAgreement.getAlbumEndNotice());
        }
        if(noticeAgreementVo.getEventNotice()^noticeAgreement.getEventNotice()){
            noticeAgreementVo.setEventNotice(noticeAgreement.getEventNotice());
        }
        if(noticeAgreementVo.getInvitationNotice()^noticeAgreement.getInvitationNotice()){
            noticeAgreementVo.setInvitationNotice(noticeAgreement.getInvitationNotice());
        }
        if(noticeAgreementVo.getOrderNotice()^noticeAgreement.getOrderNotice()){
            noticeAgreementVo.setOrderNotice(noticeAgreement.getOrderNotice());
        }

        noticeAgreementRepository.save(noticeAgreementVo);

        return noticeAgreementVo;
    }

}
