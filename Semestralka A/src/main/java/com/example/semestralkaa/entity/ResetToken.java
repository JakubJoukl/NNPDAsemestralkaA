package com.example.semestralkaa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resetTokenId;

    @Column
    private LocalDateTime validTo;

    @Column
    private boolean valid;

    @ManyToOne
    private User user;

    private String token;

    public ResetToken(User user, String token) {
        this.user = user;
        this.valid = true;
        this.validTo = LocalDateTime.now().plusHours(1);
        this.token = token;
    }
}
