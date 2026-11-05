package com.audible.content.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "audiobooks")
@Data
@NoArgsConstructor
public class Audiobook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String narrator;
    private String description;
    private String genre;
    private Integer durationSeconds;
    private String coverArtKey;   // S3 key
    private String audioKey;      // S3 key

    @Column(columnDefinition = "tsvector")
    private String searchVector;

    @OneToMany(mappedBy = "audiobook", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
