package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.PostReaction;
import me.owlsleep.owlab.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByUserIdAndPostId(Long userId, Long postId);
    long countByPostIdAndReactionType(Long postId, ReactionType reactionType);
}