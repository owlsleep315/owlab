package me.owlsleep.owlab.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String password;

    private String role = "USER"; // 기본값 USER

    @Column(name = "login_count")
    private long loginCount;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}