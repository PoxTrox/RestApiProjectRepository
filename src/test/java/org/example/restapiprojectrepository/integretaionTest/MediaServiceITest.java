package org.example.restapiprojectrepository.integretaionTest;

import jakarta.transaction.Transactional;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.repository.MediaRepository;
import org.example.restapiprojectrepository.service.MediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class MediaServiceITest {

    @Autowired
    private  WebClient webClient;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private  MediaRepository mediaRepository;
    @Transactional
    @Test
    void saveMediaToDb_shouldNotSaveDuplicates(){

        Media existingMedia = new Media();
        existingMedia.setTitle("Triangle");
        mediaRepository.saveAndFlush(existingMedia);


        mediaRepository.saveAndFlush(existingMedia);

        List<Media> allMedia = mediaRepository.findAll();
        assertEquals(1, allMedia.size(), "Only one record should exist in DB");

    }


    @Test
    void saveMediaToDb_shouldSaveNewMedia_WhenNotExists() {

        String title = "Triangle";

        mediaService.saveMediaToDb(title);
        List<Media> savedMovies = mediaRepository.findAll();

        assertFalse(savedMovies.isEmpty(), "Expected non-empty media list");
        assertEquals(title, savedMovies.get(0).getTitle());
    }

}
