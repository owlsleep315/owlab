package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WordRepository extends JpaRepository<Word, Long> {

    // 단어 존재 여부 체크
    boolean existsByWord(String word);

    // 랜덤 단어 1개 가져오기
    @Query(value = "SELECT w.word FROM words w ORDER BY RAND() LIMIT 1", nativeQuery = true)
    String findRandomWord();
}