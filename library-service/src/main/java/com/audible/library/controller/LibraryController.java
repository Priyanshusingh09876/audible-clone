package com.audible.library.controller;

import com.audible.library.model.ListeningProgress;
import com.audible.library.model.UserLibrary;
import com.audible.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<List<UserLibrary>> getUserLibrary(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(libraryService.getUserLibrary(userId));
    }

    @PostMapping("/{audiobookId}")
    public ResponseEntity<UserLibrary> addToLibrary(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String audiobookId) {
        return ResponseEntity.ok(libraryService.addToLibrary(userId, audiobookId));
    }

    @DeleteMapping("/{audiobookId}")
    public ResponseEntity<Void> removeFromLibrary(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String audiobookId) {
        libraryService.removeFromLibrary(userId, audiobookId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/progress")
    public ResponseEntity<ListeningProgress> updateProgress(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, Object> body) {
        String audiobookId = (String) body.get("audiobookId");
        Integer positionSeconds = (Integer) body.get("positionSeconds");
        Integer chapterNumber = (Integer) body.get("chapterNumber");
        return ResponseEntity.ok(libraryService.updateProgress(userId, audiobookId, positionSeconds, chapterNumber));
    }

    @GetMapping("/progress/{audiobookId}")
    public ResponseEntity<ListeningProgress> getProgress(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String audiobookId) {
        return libraryService.getProgress(userId, audiobookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
