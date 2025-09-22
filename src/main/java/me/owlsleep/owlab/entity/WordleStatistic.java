package me.owlsleep.owlab.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wordle_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordleStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private long totalGames;

    @Column(nullable = false)
    private long successCount;
}
