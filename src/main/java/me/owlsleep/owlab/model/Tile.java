package me.owlsleep.owlab.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tile {

    private char letter;
    private String color; // "green", "yellow", "gray"
}
