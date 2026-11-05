package com.audible.library.repository;

import com.audible.library.model.ListeningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListeningProgressRepository extends JpaRepository<ListeningProgress, String> {
    Optional<ListeningProgress> findByUserIdAndAudiobookId(String userId, String audiobookId);
}
