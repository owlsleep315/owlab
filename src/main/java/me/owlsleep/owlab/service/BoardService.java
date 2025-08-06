package me.owlsleep.owlab.service;

import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;
import me.owlsleep.owlab.entity.ReactionType;
import me.owlsleep.owlab.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Page<PostDto> getAllPosts(String category, Pageable pageable);

    PostDto getPostById(Long id, Long userId);

    void writePost(PostDto postDto);

    void incrementViewCount(Long postId);

    List<CommentDto> getCommentsByPostId(Long postId);

    void addComment(CommentDto commentDto);

    void deleteComment(Long commentId, String author);

    Map<String, Object> reactToPost(Long postId, Long userId, ReactionType type);

    void deletePost(Long postId);

    void updatePost(PostDto postDto);
}
