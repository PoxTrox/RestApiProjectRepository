package org.example.restapiprojectrepository.web;


import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.service.MediaService;
import org.example.restapiprojectrepository.web.dto.MediaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MediaController {


    private final MediaService mediaService;


    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;

    }


    @GetMapping("/search")
    public ResponseEntity<List<MediaResponse>> searchMovies(@RequestParam(name = "title") String title) {

        List<MediaResponse> movies = mediaService.searchMovies(title);


        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }

    @GetMapping("/returnAllByTitle")
    public ResponseEntity<List<MediaResponse>> returnMediaByTitle(@RequestParam(name = "title") String title) {
        List<Media> mediaList = mediaService.returnMediaByTitle(title);


        List<MediaResponse> responseList = mediaList.stream()
                .map(media -> MediaResponse.builder().title(media.getTitle()).poster_path(media.getPosterPath())
                        .overview(media.getOverview()).release_date(media.getReleaseDate()).build())
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(responseList);

    }


    @GetMapping()
    public ResponseEntity<?> saveMovies(@RequestParam(name = "title") String title) {

        List<Media> savedMedia = mediaService.saveMediaToDb(title);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMedia);

    }

    @PostMapping("/title")
    public ResponseEntity<Media> findMediaByTitle(@RequestParam(name = "title") String title,@RequestParam("releaseDate") String releaseDate) {
        Media mediaByTitleAndReleaseDate = mediaService.findMediaByTitleAndReleaseDate(title, releaseDate);

        return ResponseEntity.status(HttpStatus.CREATED).body(mediaByTitleAndReleaseDate);
    }

    }






