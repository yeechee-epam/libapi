package com.example.libapi.repository;

import com.example.libapi.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    Optional<Author>findByNameIgnoreCase(String name);
    List<Author> findByNameIgnoreCaseContaining(String name);
}
