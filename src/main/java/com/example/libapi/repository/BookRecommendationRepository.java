package com.example.libapi.repository;

import com.example.libapi.entity.Book;
import com.example.libapi.entity.BookRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BookRecommendationRepository extends JpaRepository<BookRecommendation,Long> {
//    List<BookRecommendation>findByBook(Book book);
    List<BookRecommendation> findByAdminName(String adminName);
//    explicit column name (adminName); else, hibernate use admin_name
//    @Query("SELECT br FROM BookRecommendation br WHERE br.adminName = :adminName AND br.book = :book")
//    Optional<BookRecommendation> findByAdminNameAndBook(String adminName, Book book);
    Optional<BookRecommendation> findByAdminNameAndBook(@Param("adminName") String adminName,
                                                        @Param("book") Book book);
}
