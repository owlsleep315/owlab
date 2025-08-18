package me.owlsleep.owlab.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponseDto {
    private Long id;
    private String gameName;
    private String imageUrl;
    private long views;
    private boolean official;
}
