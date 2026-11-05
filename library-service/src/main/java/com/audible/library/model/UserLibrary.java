package com.audible.library.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_library", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "audiobook_id"})
})
@Data
@NoArgsConstructor
public class UserLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "audiobook_id", nullable = false)
    private String audiobookId;

    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
