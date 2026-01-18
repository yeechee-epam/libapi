package com.example.libapi.service;

import com.example.libapi.dto.AuthorDto;
import com.example.libapi.entity.Author;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public Page<Author> list(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }
    public Optional<AuthorDto> getAuthorById(Long id) {
        return authorRepository.findById(id).map(authorMapper::toDto);
    }
}