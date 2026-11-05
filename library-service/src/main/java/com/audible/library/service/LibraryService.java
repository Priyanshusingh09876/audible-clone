package com.audible.library.service;

import com.audible.library.kafka.ProgressEventProducer;
import com.audible.library.model.ListeningProgress;
import com.audible.library.model.UserLibrary;
import com.audible.library.repository.ListeningProgressRepository;
import com.audible.library.repository.UserLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final UserLibraryRepository userLibraryRepository;
    private final ListeningProgressRepository progressRepository;
    private final ProgressEventProducer progressEventProducer;

    @Cacheable(value = "user-library", key = "#userId")
    public List<UserLibrary> getUserLibrary(String userId) {
        return userLibraryRepository.findByUserId(userId);
    }

    @CacheEvict(value = "user-library", key = "#userId")
    public UserLibrary addToLibrary(String userId, String audiobookId) {
        UserLibrary entry = new UserLibrary();
        entry.setUserId(userId);
        entry.setAudiobookId(audiobookId);
        return userLibraryRepository.save(entry);
    }

    @CacheEvict(value = "user-library", key = "#userId")
    public void removeFromLibrary(String userId, String audiobookId) {
        userLibraryRepository.deleteByUserIdAndAudiobookId(userId, audiobookId);
    }

    public ListeningProgress updateProgress(String userId, String audiobookId,
                                             Integer positionSeconds, Integer chapterNumber) {
        ListeningProgress progress = progressRepository
                .findByUserIdAndAudiobookId(userId, audiobookId)
                .orElse(new ListeningProgress());

        progress.setUserId(userId);
        progress.setAudiobookId(audiobookId);
        progress.setPositionSeconds(positionSeconds);
        progress.setChapterNumber(chapterNumber);
        progress.setCompleted(false);

        ListeningProgress saved = progressRepository.save(progress);

        // Publish Kafka event for real-time sync
        progressEventProducer.publishProgressEvent(userId, audiobookId, positionSeconds);

        return saved;
    }

    public Optional<ListeningProgress> getProgress(String userId, String audiobookId) {
        return progressRepository.findByUserIdAndAudiobookId(userId, audiobookId);
    }
}
