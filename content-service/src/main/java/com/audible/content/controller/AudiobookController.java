package com.audible.content.controller;

import com.audible.content.model.Audiobook;
import com.audible.content.service.AudiobookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class AudiobookController {

    private final AudiobookService audiobookService;

    @GetMapping("/audiobooks")
    public ResponseEntity<Page<Audiobook>> listAudiobooks(
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return ResponseEntity.ok(audiobookService.listAudiobooks(genre, pageable));
    }

    @GetMapping("/audiobooks/{id}")
    public ResponseEntity<Audiobook> getAudiobook(@PathVariable String id) {
        return audiobookService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/audiobooks/{id}/stream")
    public ResponseEntity<Map<String, String>> getStreamUrl(@PathVariable String id) {
        String url = audiobookService.generatePresignedStreamUrl(id);
        return ResponseEntity.ok(Map.of("streamUrl", url, "expiresIn", "3600"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Audiobook>> search(@RequestParam String q) {
        return ResponseEntity.ok(audiobookService.search(q));
    }

    @PostMapping("/audiobooks")
    public ResponseEntity<Audiobook> createAudiobook(@RequestBody Audiobook audiobook) {
        return ResponseEntity.ok(audiobookService.save(audiobook));
    }
}
