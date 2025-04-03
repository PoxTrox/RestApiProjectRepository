package org.example.restapiprojectrepository.service;


import jakarta.transaction.Transactional;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.repository.MediaRepository;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.example.restapiprojectrepository.web.dto.MediaResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


@Service
public class MediaService {

    private final WebClient webClient;
    private final MediaRepository mediaRepository;

    private static final String API_KEY = "ef00c9562509ead7242ecf5854e36594";
    private static final String REGEX ="([ .\\w']+?)(\\W\\d{4}\\W?.*)";

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
        System.out.println("Movies found: " + movies.size() + " with title " + title);

        List<Media> newMovies = new ArrayList<>();

        for (MediaResponse movie : movies) {
            Optional<Media> existingMovie = mediaRepository.findByTitle(movie.getTitle());

            if (existingMovie.isEmpty()) { // Само ако не съществува, го добавяме
                Media newMedia = new Media();
                newMedia.setTitle(movie.getTitle());
                newMedia.setOverview(movie.getOverview());
                newMedia.setReleaseDate(movie.getRelease_date());
                newMedia.setPosterPath(movie.getPoster_path());
                newMovies.add(newMedia);
            }
        }

        if (!newMovies.isEmpty()) {
            mediaRepository.saveAll(newMovies);
            mediaRepository.flush(); // Принуждава незабавно записване в базата
        }

        return Collections.emptyList(); // Връщаме празен списък, ако няма нови записи
    }

    public Media findMediaByTitleAndReleaseDate(String title, String releaseDate) {
        return mediaRepository.findByTitleAndReleaseDate(title, releaseDate).orElseThrow(() -> new RuntimeException("Media not found"));
    }


    public List<Media> returnAllMedia() {


        return mediaRepository.findAll();
    }

    public List<Media> returnMediaByTitle(String title) {

        Pattern pattern = Pattern.compile(title, Pattern.CASE_INSENSITIVE);
       // Pattern compile = Pattern.compile(REGEX);
        return mediaRepository.findAllByTitle(title).stream().filter(media -> pattern.matcher(media.getTitle()).find()).collect(toList());

    }


}