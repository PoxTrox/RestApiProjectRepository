package org.example.restapiprojectrepository.web;


import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.service.MediaService;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;



import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest (MediaController.class)
public class MediaControllerApiTest {

    @MockitoBean
    private MediaService mediaService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void getRequestGetMediaByTitle_thenReturnMediaHappyPath() throws Exception {

        MockHttpServletRequestBuilder builder = get("/api/v1/movies/search").param("title", "title");

        when(mediaService.searchMovies(any())).thenReturn(List.of(MediaResponse.builder()
                .title("title").build()));
        mockMvc.perform(builder).andExpect(status().isOk()).
                andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("title"));
        verify(mediaService, times(1)).searchMovies(any());
    }

    @Test
    void getRequestGetMediaByTitleAndReleaseDate_thenReturnMediaHappyPath() throws Exception {

        MockHttpServletRequestBuilder builder = post("/api/v1/movies/title").param("title", "title").param("releaseDate", "releaseDate");

        String title = "Test Movie";
        String releaseDate = "2024-01-01";

        Media testMedia = new Media();
        testMedia.setId(UUID.randomUUID());
        testMedia.setTitle(title);
        testMedia.setReleaseDate(releaseDate);
        testMedia.setOverview("Test overview");

        when(mediaService.findMediaByTitleAndReleaseDate(anyString(), anyString())).thenReturn(testMedia);


        mockMvc.perform(builder).andExpect(status().isCreated())
                .andExpect(jsonPath("title").value(title))
                .andExpect(jsonPath("releaseDate").value(releaseDate))
                .andExpect(jsonPath("overview").value("Test overview"));

    }

    @Test

    void tryingToTestSaveMedia() throws Exception {

        MockHttpServletRequestBuilder builder = get("/api/v1/movies").param("title","title");

        when(mediaService.saveMediaToDb("title")).thenReturn(List.of(Media.builder()
                        .title("title")
                        .releaseDate("2024-01-01")
                        .overview("Test overview")
                .build()));

        mockMvc.perform(builder)
                .andExpect(status().isCreated());
    }

    @Test
    void tryingToReturnMediaByTitleAndReleaseDate() throws Exception {

        MockHttpServletRequestBuilder builder = get("/api/v1/movies/returnAllByTitle").param("title","title");

        when(mediaService.returnMediaByTitle("title")).thenReturn(List.of(Media.builder()
                        .title("title")
                        .posterPath("posterPath")
                        .overview("overview")
                        .releaseDate("2024-01-01")
                .build()));
        List<Media> mediaList = mediaService.returnMediaByTitle("title");


        mockMvc.perform(builder).andExpect(status().isOk());
    }


}
