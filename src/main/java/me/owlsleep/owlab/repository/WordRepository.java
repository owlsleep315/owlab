package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {

    Optional<Word> findByWordIgnoreCase(String word);
}
