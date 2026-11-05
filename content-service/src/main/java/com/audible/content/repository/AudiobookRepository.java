package com.audible.content.repository;

import com.audible.content.model.Audiobook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudiobookRepository extends JpaRepository<Audiobook, String> {

    Page<Audiobook> findByGenre(String genre, Pageable pageable);

    Page<Audiobook> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    @Query(value = "SELECT * FROM audiobooks WHERE to_tsvector('english', title || ' ' || author || ' ' || COALESCE(description, '')) @@ plainto_tsquery('english', :query)", nativeQuery = true)
    List<Audiobook> fullTextSearch(@Param("query") String query);
}
