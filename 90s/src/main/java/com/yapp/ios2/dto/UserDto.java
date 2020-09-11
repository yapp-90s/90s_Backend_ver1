package com.yapp.ios2.dto;

import com.yapp.ios2.vo.NoticeAgreement;
import com.yapp.ios2.vo.User;
import lombok.*;

import javax.persistence.Column;

@Getter
@Setter
public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountInfo {
        private Long userUid;
        private String name;
        private String email;
        private String phoneNum;
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhoneNum{
        private String phoneNum;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor    public static class UserInfo{
        private Long uid;
        private String name;
        private String phoneNum;

        public UserInfo(User user){
            this.uid = user.getUid();
            this.name = user.getName();
            this.phoneNum = user.getPhone();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserProfile{
        private Integer albumTotalCount;

        private UserInfo userInfo;

        public void setUserInfo(User user){
            this.userInfo = new UserInfo(user);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeAgreement{
        private Boolean eventNotice;

        private Boolean albumEndNotice;

        private Boolean invitationNotice;

        private Boolean orderNotice;

        public NoticeAgreement(com.yapp.ios2.vo.NoticeAgreement noticeAgreement){
            this.eventNotice = noticeAgreement.getEventNotice();
            this.albumEndNotice = noticeAgreement.getAlbumEndNotice();
            this.invitationNotice = noticeAgreement.getInvitationNotice();
            this.orderNotice = noticeAgreement.getOrderNotice();
        }
    }

}
