package me.owlsleep.owlab.service;

import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    Page<PostDto> getAllPosts(String category, Pageable pageable);

    PostDto getPostById(Long id);

    void writePost(PostDto postDto);

    void incrementViewCount(Long postId);

    List<CommentDto> getCommentsByPostId(Long postId);

    void addComment(CommentDto commentDto);

    void deleteComment(Long commentId, String author);

    void likePost(Long postId);

    void dislikePost(Long postId);

    void deletePost(Long postId);

    void updatePost(PostDto postDto);
}
