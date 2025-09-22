package me.owlsleep.owlab.repository;

import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.entity.WordleStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordleStatisticRepository extends JpaRepository<WordleStatistic, Long> {

    Optional<WordleStatistic> findByUser(User user);

    List<WordleStatistic> findTop10ByOrderBySuccessCountDescTotalGamesAscIdAsc();

    List<WordleStatistic> findAllByOrderBySuccessCountDescTotalGamesAscIdAsc();
}
