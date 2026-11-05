package com.audible.library.repository;

import com.audible.library.model.UserLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLibraryRepository extends JpaRepository<UserLibrary, String> {
    List<UserLibrary> findByUserId(String userId);
    void deleteByUserIdAndAudiobookId(String userId, String audiobookId);
}
