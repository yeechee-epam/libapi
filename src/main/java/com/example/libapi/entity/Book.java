package com.example.libapi.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",length = 50,nullable = false,unique = true)
    private String name;

    public Book(String bookName) {
        this.name=bookName;
    }
//    @ManyToOne
//    @JoinColumn(name = "author_id",nullable = false)
//    private Author author;
}