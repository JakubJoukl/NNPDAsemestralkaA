package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

public class ResetPasswordDto {
    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private String password;
}
