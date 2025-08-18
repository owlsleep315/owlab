package me.owlsleep.owlab.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String gameName;

    @Column(length = 500)
    private String couponUsage;

    @Column(length = 500)
    private String couponLink;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private long views = 0L;

    @Column(nullable=false)
    private boolean official = false;

    private LocalDateTime createdAt;
}
