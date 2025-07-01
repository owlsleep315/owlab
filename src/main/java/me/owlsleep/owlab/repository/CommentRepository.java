package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.Comment;
import me.owlsleep.owlab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글의 댓글 중, 부모가 없는 댓글만 (= 최상위 댓글), 최신순
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

}
