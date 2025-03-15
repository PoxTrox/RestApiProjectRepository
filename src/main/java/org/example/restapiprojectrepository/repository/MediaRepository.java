package org.example.restapiprojectrepository.repository;

import org.example.restapiprojectrepository.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {



    Optional<Media> findByTitle(String title);

    Optional<Media> findByTitleAndReleaseDate(String title, String releaseDate);



}
