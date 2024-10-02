package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

public class ChangePasswordDto {
    @Getter
    @Setter
    private String jwtToken;

    @Getter
    @Setter
    private String newPassword;
}
