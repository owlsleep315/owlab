package me.owlsleep.owlab.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private Long postId;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private Long parentId;
    private List<CommentDto> replies = new ArrayList<>();
}
