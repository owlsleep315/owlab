package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.Post;
import me.owlsleep.owlab.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 카테고리별 게시글 조회 (최신순)
    Page<Post> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

    // 전체 게시글 최신순 조회
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
