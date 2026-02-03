package com.example.libapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookrecommendations",
uniqueConstraints = @UniqueConstraint(columnNames = {"adminName","book_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(name = "adminName", nullable = false)
//    tell hibernate to use exact name "adminName" instead of admin_name
//    by default, Hibernate converts Java camelCase fields to snake_case column names unless you explicitly tell it otherwise.
@Column(name = "\"adminName\"", nullable = false)
    private String adminName;
//    private Long bookId;
    @ManyToOne
    @JoinColumn(name="book_id",nullable = false)
    private Book book;

}
