package org.example.restapiprojectrepository.service;

import aj.org.objectweb.asm.commons.Remapper;
import jakarta.transaction.Transactional;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.repository.MediaRepository;
import org.example.restapiprojectrepository.web.dto.MediaRequest;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.example.restapiprojectrepository.web.dto.MediaResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class MediaService {

    private final WebClient webClient;
    private final MediaRepository mediaRepository;

    private static final String API_KEY = "ef00c9562509ead7242ecf5854e36594";

    @Autowired
    public MediaService(WebClient webClient, MediaRepository mediaRepository) {
        this.webClient = webClient;
        this.mediaRepository = mediaRepository;
    }


    public List<MediaResponse> searchMovies(String title) {
        String url = "/search/movie?query={title}&api_key={apiKey}";

        try {
            MediaResponseWrapper response = webClient.get()
                    .uri(url, title, API_KEY)
                    .retrieve()
                    .bodyToMono(MediaResponseWrapper.class)
                    .block();

            return response != null ? response.getResults() : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<Media> saveMediaToDb(String title) {

        List<MediaResponse> movies = searchMovies(title);
        System.out.println("Movies found: " + movies.size());

        List<Media> newMovies = new ArrayList<>();
        List<Media> media = returnAllMedia();

        Stream<Media> mediaStream = media.stream().filter(e -> e.getTitle().equalsIgnoreCase(title));
        if (mediaStream.findFirst().isEmpty()) {
            for (MediaResponse movie : movies) {
                Optional<Media> existingMovie = mediaRepository.findByTitle(movie.getTitle());


                if (existingMovie.isEmpty() && title.equalsIgnoreCase(movie.getTitle())) {
                    Media newMedia = new Media();
                    newMedia.setTitle(movie.getTitle());
                    newMedia.setOverview(movie.getOverview());
                    newMedia.setReleaseDate(movie.getRelease_date());
                    newMedia.setPosterPath(movie.getPoster_path());
                    newMovies.add(newMedia);
                }

            }

        }
        return mediaRepository.saveAll(newMovies);

    }

    public Media findMediaByTitleAndReleaseDate(String title, String releaseDate) {
        return mediaRepository.findByTitleAndReleaseDate(title, releaseDate).orElseThrow(() -> new RuntimeException("Media not found"));
    }

    public List<Media> returnAllMedia(){
        return mediaRepository.findAll();
    }
}