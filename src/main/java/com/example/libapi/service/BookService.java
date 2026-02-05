package com.example.libapi.service;

import com.example.libapi.dto.BookDto;
import com.example.libapi.entity.Author;
import com.example.libapi.entity.Book;
//import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.entity.BookRecommendation;
import com.example.libapi.exception.DuplicateBookException;
import com.example.libapi.exception.ResourceNotFoundException;
import com.example.libapi.mapper.BookMapper;
import com.example.libapi.repository.AuthorRepository;
import com.example.libapi.repository.BookRepository;
import com.example.libapi.repository.BookRecommendationRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    private final BookRecommendationRepository bookRecommendationRepository;
public BookService(BookRepository bookRepository,AuthorRepository authorRepository,BookMapper bookMapper,BookRecommendationRepository bookRecommendationRepository)
    {
        this.bookRepository=bookRepository;
        this.authorRepository=authorRepository;
        this.bookMapper=bookMapper;
        this.bookRecommendationRepository=bookRecommendationRepository;
    }

    public Page<BookDto> list(Pageable pageable)
    {
        return bookRepository
                .findAll(pageable)
                .map(bookMapper::toDto);
    }

//    public Optional<BookDto> getBookById(Long id) {
//        return bookRepository.findById(id).map(bookMapper::toDto);
//    }
//    map dto to include author link
public Optional<BookDto> getBookById(Long id) {
    return bookRepository.findById(id).map(book -> {
        BookDto dto = bookMapper.toDto(book);
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            dto.setAuthorLink("/authors/" + book.getAuthor().getId());
        }
//        to show recommend icon or not
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"))) {

            String adminName = auth.getName();
            boolean recommended = bookRecommendationRepository
                    .findByAdminNameAndBook(adminName, book)
                    .isPresent();

            dto.setRecommendedByMe(recommended);
        } else {
            dto.setRecommendedByMe(false);
        }

        return dto;
    });

}

    public Page<BookDto> findBooksByAuthorId(Long authorId, Pageable pageable) {
        // Ensure author exists, else throw 404
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        // Map books to BookDto, including authorLink
        return bookRepository.findByAuthorId(authorId, pageable)
                .map(book -> {
                    BookDto dto = bookMapper.toDto(book);
                    dto.setAuthorLink("/authors/" + authorId);
                    return dto;
                });
    }

    public boolean bookExists(String bookName, String authorName) {
        return bookRepository.findByNameIgnoreCaseAndAuthor_NameIgnoreCase(bookName.trim(), authorName.trim()).isPresent();
    }


    //create new book
//@Transactional
//public BookDto createBook(BookDto bookDto) {
//    // Find or create author
//    Author author;
//    if (bookDto.getAuthorId() != null) {
//        author = authorRepository.findById(bookDto.getAuthorId())
//                .orElseGet(() -> authorRepository.save(
//                        Author.builder().name(bookDto.getAuthorName()).build()));
//    } else if (bookDto.getAuthorName() != null && !bookDto.getAuthorName().isBlank()) {
//        author = authorRepository.findByNameIgnoreCase(bookDto.getAuthorName())
//                .orElseGet(() -> authorRepository.save(
//                        Author.builder().name(bookDto.getAuthorName().trim()).build()));
//    } else {
//        throw new IllegalArgumentException("Author information is required");
//    }
//
//    // Create and save book
//    Book book = Book.builder()
//            .name(bookDto.getName())
//            .author(author)
//            .build();
//    Book saved = bookRepository.save(book);
//
//    // Map to DTO and set authorLink
//    BookDto result = bookMapper.toDto(saved);
//    result.setAuthorLink("/authors/" + author.getId());
//    return result;
//}

    @Transactional
    public Book create(BookDto bookDto) {
//    logging user info
        Authentication authentication= SecurityContextHolder.getContext()
                .getAuthentication();
        String username=authentication.getName();
//        log email or custom claims
        String email=null;
        if(authentication instanceof JwtAuthenticationToken jwtAuth)
        {
            Object emailClaim=jwtAuth.getToken().getClaims().get("email");
            if(emailClaim!=null)email=emailClaim.toString();
//            Object rolesClaim=jwtAuth.getToken().getClaims().get("https://spring-boot-example/roles");
//            if(rolesClaim!=null)roles=C
//            roles = admin/user
//            authorities=can edit/delete/etc books

        }
        System.out.println("A book has been created by: "+username+((email!=null)?email:""));
        //A book has been created by: github|xxx

        // Find or create author by name (case-insensitive)
        Author author = authorRepository.findByNameIgnoreCase(bookDto.getAuthorName().trim())
                .orElseGet(() -> authorRepository.save(
                        Author.builder().name(bookDto.getAuthorName().trim()).build()));

        // Check for duplicate book (same name and author)
        boolean duplicate = bookRepository
                .findByNameIgnoreCaseAndAuthor_NameIgnoreCase(bookDto.getName().trim(), author.getName().trim())
                .isPresent();
        if (duplicate) {
            throw new DuplicateBookException("Book with this name and author already exists");
        }

        // Create and save book
        Book book = Book.builder()
                .name(bookDto.getName().trim())
                .author(author)
                .build();
        return bookRepository.save(book);
    }
    @Transactional
    public Book updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Find author if there is id or create author by name (case-insensitive)
//        Author author = authorRepository.findByNameIgnoreCase(bookDto.getAuthorName().trim())
//                .orElseGet(() -> authorRepository.save(
//                        Author.builder().name(bookDto.getAuthorName().trim()).build()));
        Author author = null;
        if (bookDto.getAuthorId() != null) {
            author = authorRepository.findById(bookDto.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthorId()));
        } else if (bookDto.getAuthorName() != null && !bookDto.getAuthorName().isBlank()) {
            author = authorRepository.findByNameIgnoreCase(bookDto.getAuthorName().trim())
                    .orElseGet(() -> authorRepository.save(
                            Author.builder().name(bookDto.getAuthorName().trim()).build()));
        } else {
            throw new IllegalArgumentException("Author information is required");
        }
        // Check for duplicate book (same name and author, but different ID)
        bookRepository.findByNameIgnoreCaseAndAuthor_NameIgnoreCase(bookDto.getName().trim(), author.getName().trim())
                .ifPresent(existingBook -> {
                    if (!existingBook.getId().equals(id)) {
                        throw new DuplicateBookException("Book with this name and author already exists");
                    }
                });


        // Update book fields
        book.setName(bookDto.getName().trim());
        book.setAuthor(author);

        return bookRepository.save(book);
    }
//    delete
@Transactional
public void deleteBook(Long id) {
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    bookRepository.delete(book);
}

//recommend
@Transactional
public boolean toggleRecommendBook(Long bookId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
        throw new RuntimeException("Unauthorized"); // or other custom exception
    }
    String adminName = auth.getName();
//fetch book
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Book not found with id: " + bookId));
//check if recommendation already exists
    Optional<BookRecommendation> existing =
            bookRecommendationRepository.findByAdminNameAndBook(adminName, book);

    if (existing.isPresent()) {
        // UNRECOMMEND ie remove row fr rec table
        bookRecommendationRepository.delete(existing.get());
        return false; // now NOT recommended
    } else {
        // RECOMMEND ie create row in rec table
        BookRecommendation rec = BookRecommendation.builder()
                .adminName(adminName)
                .book(book)
                .build();
        bookRecommendationRepository.save(rec);
        return true; // now recommended
    }
}

    //    get recommended books by an admin
public List<Book> getBooksRecommendedByCurrentAdmin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String adminName = auth.getName();

    return bookRecommendationRepository.findByAdminName(adminName)
            .stream()
            .map(BookRecommendation::getBook)
            .toList();
}


}
