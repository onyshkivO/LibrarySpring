package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotFoundException;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotSavedException;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebMvcTest(PublicationService.class)
public class PublicationServiceTest {

    @MockBean
    private PublicationRepository publicationRepository;

    @InjectMocks
    @Autowired
    private PublicationService publicationService;

    @Test
    public void getPublicationByIdWhenExistTest() {
        when(publicationRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Publication(1, "Publication")));

        Publication publication = publicationService.getPublicationById(1);
        assertThat(publication.getId()).isEqualTo(1);
        assertThat(publication.getName()).isEqualTo("Publication");
    }

    @Test
    public void getPublicationByIdWhenNotExistTest() {
        when(publicationRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicationService.getPublicationById(anyInt()))
                .isInstanceOf(PublicationNotFoundException.class);
        verify(publicationRepository, times(1)).findById(anyInt());
    }

    @Test
    public void getPublicationsTest() {
        Page<Publication> publicationPage = new PageImpl<>(List.of(
                new Publication(1, "Publication1"),
                new Publication(2, "Publication2"),
                new Publication(3, "Publication3")
        ));
        when(publicationRepository.findAll((Pageable) any()))
                .thenReturn(publicationPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<Publication> publicationPageResult = publicationService.getAllPublications(pageable);
        List<Publication> publications = publicationPageResult.getData();
        assertThat(publications.get(0).getId()).isEqualTo(1);
        assertThat(publications.get(0).getName()).isEqualTo("Publication1");
        assertThat(publications.get(1).getId()).isEqualTo(2);
        assertThat(publications.get(1).getName()).isEqualTo("Publication2");
        assertThat(publications.get(2).getId()).isEqualTo(3);
        assertThat(publications.get(2).getName()).isEqualTo("Publication3");
        verify(publicationRepository, times(1)).findAll((Pageable) any());
    }

    @Test
    public void createPublicationWithoutIdTest() {
        Publication publication = new Publication("Publication");
        when(publicationRepository.save(any())).thenReturn(publication);

        publication = publicationService.savePublication(publication);
        assertThat(publication.getName()).isEqualTo("Publication");
    }

    @Test
    public void createPublicationWithIdTest() {
        Publication publication = new Publication(1, "Publication");
        assertThatThrownBy(() -> publicationService.savePublication(publication))
                .isInstanceOf(PublicationNotSavedException.class);
        verifyNoInteractions(publicationRepository);
    }

    @Test
    public void updatePublicationTest() {
        Publication publicationFromdb = new Publication(1, "Publication");
        Publication publication = new Publication("Publication(Updated)");
        when(publicationRepository.findById(any())).thenReturn(Optional.of(publicationFromdb));
        publicationFromdb = publicationService.updatePublication(1, publication);
        assertThat(publicationFromdb.getId()).isEqualTo(1);
        assertThat(publicationFromdb.getName()).isEqualTo("Publication(Updated)");
        verify(publicationRepository, times(1)).findById(anyInt());
    }

    @Test
    public void deletePublicationTest() {
        when(publicationRepository.findById(any())).thenReturn(Optional.of(new Publication()));
        publicationService.delete(anyInt());
        verify(publicationRepository).delete(any());
        verify(publicationRepository, times(1)).findById(anyInt());
    }


}
