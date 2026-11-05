package com.audible.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "listening_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "audiobook_id"})
})
@Data
@NoArgsConstructor
public class ListeningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "audiobook_id", nullable = false)
    private String audiobookId;

    private Integer positionSeconds;
    private Integer chapterNumber;
    private Boolean completed;
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
