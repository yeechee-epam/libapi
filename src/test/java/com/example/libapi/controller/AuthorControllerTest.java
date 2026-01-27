package com.example.libapi.controller;

import com.example.libapi.config.ApplicationProperties;
import com.example.libapi.dto.AuthorDto;
import com.example.libapi.service.AuthorService;
import com.example.libapi.mapper.AuthorMapper;
import com.example.libapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@org.springframework.test.context.TestPropertySource(properties = {

//1/26-to disable all oauth2
        "spring.autoconfigure.exclude=org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientWebSecurityAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration,org.springframework.boot.security.oauth2.server.resource.autoconfigure.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
})
//1/26-2 bcos there is a conflict btw security config n main n testsecurityconfig class in test
//@ActiveProfiles({"test"})//no need; test container will inject db config

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private AuthorMapper authorMapper;

    @MockitoBean
    private BookService bookService;
//1/27
    @MockitoBean
    private ApplicationProperties applicationProperties;
/*why add ApplicationProperties bean here even though this test does not need clientOriginUrl
is bcos ApplicationProperties bean is used by ApplicationConfig which is needed for CORS of backend.
Controller test classes depend on ApplicationConfig which set web context for all controllers
* */
    @Test
    @DisplayName("GET /authors returns paginated list of authors with 200 OK")
    void testListAuthors() throws Exception {
        // Arrange
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(1L);
        authorDto.setName("Author Name");

        Page<AuthorDto> authorPage = new PageImpl<>(List.of(authorDto), PageRequest.of(0, 10), 1);

        Mockito.when(authorService.list(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        Mockito.when(authorMapper.toDto(any())).thenReturn(authorDto);

        // Act & Assert
        mockMvc.perform(get("/authors")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // The following checks depend on your actual JSON structure
                .andExpect(jsonPath("$.content").isArray());
    }
}