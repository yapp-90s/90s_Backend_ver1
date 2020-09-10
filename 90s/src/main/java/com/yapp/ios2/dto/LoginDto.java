package com.yapp.ios2.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String emailKakao;
    private String emailApple;
    private String emailGoogle;
}
