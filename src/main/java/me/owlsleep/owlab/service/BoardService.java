package me.owlsleep.owlab.service;

import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;

import java.util.List;

public interface BoardService {

    List<PostDto> getAllPosts(String category);

    PostDto getPostById(Long id);

    void writePost(PostDto postDto);

    void incrementViewCount(Long postId);

    List<CommentDto> getCommentsByPostId(Long postId);

    void addComment(CommentDto commentDto);

    void deleteComment(Long commentId, String author);

    void likePost(Long postId);

    void dislikePost(Long postId);

    void deletePost(Long postId, String author);

    void updatePost(PostDto postDto, String author);
}
