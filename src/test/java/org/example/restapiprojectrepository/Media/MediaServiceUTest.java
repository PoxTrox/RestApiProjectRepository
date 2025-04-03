package org.example.restapiprojectrepository.Media;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.repository.MediaRepository;
import org.example.restapiprojectrepository.service.MediaService;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.example.restapiprojectrepository.web.dto.MediaResponseWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static reactor.core.publisher.Mono.from;
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

    @Mock
    private MockWebServer mockWebServer;

//    @Spy
//    private MediaService spyMediaService;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        mediaService = new MediaService(webClient, mediaRepository);
    }
//    @BeforeEach
//    void setUp() {
//        MediaService realService = new MediaService(webClient,mediaRepository); // Създаваме истинския обект
//        spyMediaService = Mockito.spy(realService); // Създаваме `spy` ръчно
//    }

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

    @Test
    void TryingToSearchMovieInDatabase_NotSuccessfully() throws Exception {

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
                .setResponseCode(404)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        List<MediaResponse> results = mediaService.searchMovies("Test");

        assertEquals(0, results.size());
//        assertEquals("Test Movie", results.get(0).getTitle());
//        assertEquals("Test overview", results.get(0).getOverview());

    }

    @Test
    void returnMediaByGivenTitleCASE_INSENSITIVE_successfully() throws Exception {

        String title = "Random Title";
        Pattern pattern = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
        List<Media>mediaList = List.of(Media.builder().title(title).build(), Media.builder().title(title).build());

        Mockito.when(mediaRepository.findAllByTitle(title)).thenReturn(mediaList.stream().filter(media -> pattern.matcher(media.getTitle()).find()).collect(toList()));

        List<Media> media = mediaService.returnMediaByTitle(title);

        assertThat(media).isNotEmpty();
        verify(mediaRepository, times(1)).findAllByTitle(title);
    }

//    @Test
//    void tryingToSaveMediaToDatabase_Successfully() throws Exception {
//
//        String title = "Inception";
//        List<MediaResponse> mockMovies = List.of( MediaResponse.builder()
//                .title("Test Subject").release_date("2025-01-01")
//                .overview("This is Random text").build(),MediaResponse.builder()
//                 .title("Good title").release_date("2000-01-01").overview("This is Random text text").build(),MediaResponse.builder()
//                .build());
//
//
//
//        Mockito.doReturn(mockMovies)
//                .when(mediaRepository)
//                .findAllByTitle(title);
//     // Mockito.when(mediaService.searchMovies(title)).thenReturn(mockMovies);
//        List<MediaResponse> mediaResponses = mediaService.searchMovies(title);
//
//       // Mockito.when(mediaRepository.findByTitle(anyString()))
//           //     .thenReturn(Optional.empty()); // Филмите не съществуват в DB
//
//        // Act
//        List<Media> savedMovies = mediaService.saveMediaToDb(title);
//
//        // Assert
//       // assertEquals(2, savedMovies.size()); // Очакваме 2 нови записа
////        verify(mediaRepository, times(1)).saveAll(anyList()); // Проверяваме дали `saveAll` е извикан веднъж
////        verify(mediaRepository, times(1)).saveAll(anyList());
//
//
//    }
//
//    @Test
//    void saveMediaToDb_ShouldNotSaveMovies_WhenMoviesAlreadyExistInDb() {
//        // Arrange
//        String title = "Inception";
//        List<MediaResponse> mockMovies = List.of(MediaResponse.builder()
//                .title("Test Subject").release_date("2025-01-01")
//                .overview("This is Random text").build(), MediaResponse.builder()
//                .title("Good title").release_date("2000-01-01").overview("This is Random text text").build(), MediaResponse.builder()
//                .build());
//
//      // Mockito.lenient().doReturn(mockMovies).when(mediaService).searchMovies(title);
//  //   Mockito.lenient().when(mediaService.searchMovies(title)).thenReturn(mockMovies);
//       // List<MediaResponse> mediaResponses = mediaService.searchMovies(title);
////        Mockito.when(mediaRepository.findByTitle(anyString()))
////                .thenReturn(Optional.empty()); // Филмите не съществуват в DB
//
//        // Act
//        List<Media> savedMovies = mediaService.saveMediaToDb(title);
//
//        // Assert
//      //  assertEquals(2, savedMovies.size()); // Очакваме 2 нови записа
//        verify(mediaRepository, times(1)).saveAll(anyList()); // Проверявам
//    }


//    @Test
//    void saveMediaToDb_ShouldNotSaveMovies_WhenMoviesAlreadyExistInDb() {
//        // Arrange
//        String title = "Inception";
//        List<MediaResponse> mockMovies = List.of(MediaResponse.builder()
//                .title("Inception").release_date("2025-01-01")
//                .overview("This is Random text").build(), MediaResponse.builder()
//                .title("Inception Triangle").release_date("2000-01-01").overview("This is Random text text").build(), MediaResponse.builder()
//                .build());
//
//        Media media = Media.builder()
//                .title("test")
//                .releaseDate("27.03.2014")
//                .overview("Test overview")
//                .build();
//
//        Mockito.when(mediaRepository.findByTitle("test")).thenReturn(Optional.of(media));
//       // Mockito.when(mediaService.searchMovies(title)).thenReturn(mockMovies);
////        String jsonResponse = """
////                {
////                  "results": [
////                    {
////                      "title": "Test Movie",
////                      "overview": "Test overview"
////                    }
////                  ]
////                }
////                """;
////
////        mockWebServer.enqueue(new MockResponse()
////                .setResponseCode(200)
////                .setBody(jsonResponse)
////                .addHeader("Content-Type", "application/json"));
////
//       List<MediaResponse> results = mediaService.searchMovies("Test");
//
//
//      //  verify(webClient, times(1));
////        assertEquals("Test Movie", results.get(0).getTitle());
////        assertEquals("Test overview", results.get(0).getOverview());
//        verify(mediaRepository, times(1)).findByTitle("test");
//
//
//    }
}
