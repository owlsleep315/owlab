package me.owlsleep.owlab.entity;

import jakarta.persistence.*;
import lombok.*;

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

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}