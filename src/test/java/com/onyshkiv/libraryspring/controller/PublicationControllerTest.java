package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.service.PublicationService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PublicationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PublicationService publicationService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getPublicationByIdWhenExistTest() throws Exception {
        when(publicationService.getPublicationById(anyInt()))
                .thenReturn(new Publication(1, "Publication1"));
        mockMvc.perform(get("/publications/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Publication1"));
        verify(publicationService, times(1)).getPublicationById(anyInt());
    }

    @Test
    public void getPublicationsTest() throws Exception {
        List<Publication> publicationList = List.of(
                new Publication(1, "Publication1"),
                new Publication(2, "Publication2"),
                new Publication(3, "Publication3")
        );
        when(publicationService.getAllPublications(any())).thenReturn(new DataPageDto<>(publicationList, 0, 1));
        mockMvc.perform(get("/publications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Publication1"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Publication2"))
                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].name").value("Publication3"));
    }

    @Test
    public void savePublicationWithoutErrorsTest() throws Exception {
        Publication publication = new Publication(1, "test Publication");
        String jsonPublication = objectMapper.writeValueAsString(publication);

        when(publicationService.savePublication(any()))
                .thenReturn(publication);

        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPublication)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test Publication"));
        verify(publicationService, times(1)).savePublication(any());
    }

    @Test
    public void savePublicationWithValidationErrorTest() throws Exception {
        Publication publication = new Publication(1, "");
        String jsonPublication = objectMapper.writeValueAsString(publication);


        mockMvc.perform(post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPublication)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        verifyNoInteractions(publicationService);
    }

    @Test
    public void updatePublicationTest() throws Exception {
        Publication publication = new Publication(1, "test Publication");
        String jsonPublication = objectMapper.writeValueAsString(publication);


        when(publicationService.updatePublication(anyInt(), any()))
                .thenReturn(publication);

        mockMvc.perform(put("/publications/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPublication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test Publication"));
        verify(publicationService, times(1)).updatePublication(anyInt(), any());
    }

    @Test
    public void deletePublicationTest() throws Exception {
        mockMvc.perform(delete("/publications/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
