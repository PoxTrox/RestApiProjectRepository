package org.example.restapiprojectrepository.Media;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.repository.MediaRepository;
import org.example.restapiprojectrepository.service.MediaService;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.example.restapiprojectrepository.web.dto.MediaResponseWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class MediaServiceUTest {


    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private WebClient webClient;


    @Mock
    @SuppressWarnings("raw String")
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("raw String")
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MediaService mediaService;

    private MockWebServer mockWebServer;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        mediaService = new MediaService(webClient, mediaRepository);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void tryingToReturnAllMedia_Successfully() throws Exception {

        List<Media> list = List.of(Media.builder().build(), Media.builder().build(), Media.builder().build());

        Mockito.when(mediaRepository.findAll()).thenReturn(list);

        List<Media> media = mediaService.returnAllMedia();

        assertThat(media).isNotEmpty();
        verify(mediaRepository, times(1)).findAll();
    }

    @Test
    void tryingToReturnMediaByGivenTitleAndReleaseDate_Successfully() throws Exception {

        Media media = Media.builder()
                .title("test")
                .releaseDate("27.03.2014")
                .overview("test")
                .build();

        Mockito.when(mediaRepository.findByTitleAndReleaseDate((anyString()), anyString())).thenReturn(Optional.ofNullable(media));
        Media mediaByTitleAndReleaseDate = mediaService.findMediaByTitleAndReleaseDate("test", "27.03.2014");
        assertThat(mediaByTitleAndReleaseDate).isNotNull();
        assertThat(mediaByTitleAndReleaseDate.getTitle()).isEqualTo("test");
        assertThat(mediaByTitleAndReleaseDate.getReleaseDate()).isEqualTo("27.03.2014");
        verify(mediaRepository, times(1)).findByTitleAndReleaseDate((anyString()), anyString());

    }

    @Test
    void tryingToReturnMediaByGivenTitleAndReleaseDate_throwException() throws Exception {

        Media media = Media.builder()
                .title("test")
                .releaseDate("27.03.2014")
                .overview("test")
                .build();
        Mockito.when(mediaRepository.findByTitleAndReleaseDate((anyString()), anyString())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> mediaService.findMediaByTitleAndReleaseDate("test", "27.03.2014"));

    }

    @Test
    void TryingToSearchMovieInDatabase_Successfully() throws Exception {

        String jsonResponse = """
                {
                  "results": [
                    {
                      "title": "Test Movie",
                      "overview": "Test overview"
                    }
                  ]
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        List<MediaResponse> results = mediaService.searchMovies("Test");

        assertEquals(1, results.size());
        assertEquals("Test Movie", results.get(0).getTitle());
        assertEquals("Test overview", results.get(0).getOverview());

    }

}
