package com.example.semestralkaa.dto;

import lombok.Getter;
import lombok.Setter;

public class ChangePasswordDto {
    @Getter
    @Setter
    private String oldPassword;

    @Getter
    @Setter
    private String newPassword;
}
