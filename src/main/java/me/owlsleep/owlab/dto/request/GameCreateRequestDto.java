package me.owlsleep.owlab.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameCreateRequestDto {
    private String gameName;
    private String couponUsage;
    private String couponLink;
    private String imageUrl;
}