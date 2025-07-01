package me.owlsleep.owlab.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {

    private String name;
    private String username;
    private String password;
}
