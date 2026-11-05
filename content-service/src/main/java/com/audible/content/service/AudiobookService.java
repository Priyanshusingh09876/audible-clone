package com.audible.content.service;

import com.audible.content.model.Audiobook;
import com.audible.content.repository.AudiobookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AudiobookService {

    private final AudiobookRepository audiobookRepository;
    private final S3Presigner s3Presigner;

    private static final String BUCKET = System.getenv().getOrDefault("AWS_S3_BUCKET", "audible-clone-audio");

    public Page<Audiobook> listAudiobooks(String genre, Pageable pageable) {
        if (genre != null && !genre.isBlank()) {
            return audiobookRepository.findByGenre(genre, pageable);
        }
        return audiobookRepository.findAll(pageable);
    }

    public Optional<Audiobook> getById(String id) {
        return audiobookRepository.findById(id);
    }

    public List<Audiobook> search(String query) {
        return audiobookRepository.fullTextSearch(query);
    }

    public String generatePresignedStreamUrl(String audiobookId) {
        Audiobook audiobook = audiobookRepository.findById(audiobookId)
                .orElseThrow(() -> new RuntimeException("Audiobook not found: " + audiobookId));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(audiobook.getAudioKey())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public Audiobook save(Audiobook audiobook) {
        return audiobookRepository.save(audiobook);
    }
}
