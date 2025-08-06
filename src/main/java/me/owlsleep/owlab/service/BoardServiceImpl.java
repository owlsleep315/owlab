package me.owlsleep.owlab.service;

import lombok.RequiredArgsConstructor;
import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;
import me.owlsleep.owlab.entity.*;
import me.owlsleep.owlab.repository.CommentRepository;
import me.owlsleep.owlab.repository.PostReactionRepository;
import me.owlsleep.owlab.repository.PostRepository;
import me.owlsleep.owlab.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

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
    @Transactional(readOnly = true)
    public PostDto getPostById(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        PostDto postDto = toDto(post);

        postDto.setLikeCount(postReactionRepository.countByPostIdAndReactionType(post.getId(), ReactionType.LIKE));
        postDto.setDislikeCount(postReactionRepository.countByPostIdAndReactionType(post.getId(), ReactionType.DISLIKE));

        if (userId != null) {
            String userReaction = postReactionRepository.findByUserIdAndPostId(userId, id)
                    .map(r -> r.getReactionType().name())
                    .orElse(null);
            postDto.setUserReaction(userReaction);
        }

        return postDto;
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

        commentRepository.delete(comment);
    }

    // 추천/비추천
    @Override
    @Transactional
    public Map<String, Object> reactToPost(Long postId, Long userId, ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<PostReaction> existing = postReactionRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            PostReaction reaction = existing.get();
            if (reaction.getReactionType() == type) {
                // 같은 버튼 다시 누르면 취소
                postReactionRepository.delete(reaction);
            } else {
                // 반대 반응으로 변경
                reaction.setReactionType(type);
            }
        } else {
            // 최초 반응
            PostReaction newReaction = new PostReaction();
            newReaction.setPost(post);
            User userRef = userRepository.getReferenceById(userId);
            newReaction.setUser(userRef);
            newReaction.setReactionType(type);
            postReactionRepository.save(newReaction);
        }

        long likeCount = postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.LIKE);
        long dislikeCount = postReactionRepository.countByPostIdAndReactionType(postId, ReactionType.DISLIKE);

        return Map.of("likes", likeCount, "dislikes", dislikeCount);
    }


    // 게시글 삭제
    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        postRepository.delete(post);
    }

    // 게시글 수정
    @Override
    public void updatePost(PostDto dto) {
        Post post = postRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

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