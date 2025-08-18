package me.owlsleep.owlab.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDto {
    private Long id;
    private String gameName;
    private String couponUsage;
    private String couponLink;
    private String imageUrl;
    private long views;
    private boolean official;
    private LocalDateTime createdAt;
}
