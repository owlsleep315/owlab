package me.owlsleep.owlab.service;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;
import me.owlsleep.owlab.entity.Comment;
import me.owlsleep.owlab.entity.Post;
import me.owlsleep.owlab.repository.CommentRepository;
import me.owlsleep.owlab.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 전체 글 조회 (카테고리별 필터링)
    @Override
    public Page<PostDto> getAllPosts(String category, Pageable pageable) {
        Page<Post> posts;
        if (category != null && !category.isBlank()) {
            posts = postRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return posts.map(this::toDto);
    }

    // 게시글 상세 조회
    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return toDto(post);
    }

    // 게시글 작성
    @Override
    public void writePost(PostDto dto) {
        Post post = new Post();
        post.setCategory(dto.getCategory());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // 조회수 증가
    @Override
    public void incrementViewCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }

    // 댓글 조회
    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
        return Optional.ofNullable(comments)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    // 댓글 등록 (답글 포함)
    @Override
    public void addComment(CommentDto dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(dto.getAuthor());
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        if (dto.getParentId() != null) {
            Comment parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            comment.setParent(parent);
        }

        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Override
    public void deleteComment(Long commentId, String author) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 추천
    @Override
    public void likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
    }

    // 비추천
    @Override
    public void dislikePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        post.setDislikes(post.getDislikes() + 1);
        postRepository.save(post);
    }

    // 게시글 삭제
    @Override
    public void deletePost(Long postId, String author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getAuthor().equals(author)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 게시글 수정
    @Override
    public void updatePost(PostDto dto, String author) {
        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getAuthor().equals(author)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    // ===== DTO 변환 메서드 =====

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCategory(post.getCategory());
        dto.setAuthor(post.getAuthor());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setViews(post.getViews());
        dto.setLikes(post.getLikes());
        dto.setDislikes(post.getDislikes());
        return dto;
    }

    private CommentDto toCommentDto(Comment comment) {
        List<CommentDto> replyDtos = comment.getReplies() != null
                ? comment.getReplies().stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(replyDtos)
                .build();
    }
}