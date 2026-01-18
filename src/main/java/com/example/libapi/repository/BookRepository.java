package com.example.libapi.repository;

import com.example.libapi.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {
    //change List to Page for pagination of result
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    Optional<Book> findByNameIgnoreCaseAndAuthor_NameIgnoreCase(String name, String authorName);

}
