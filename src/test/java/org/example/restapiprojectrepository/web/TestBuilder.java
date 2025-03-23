package org.example.restapiprojectrepository.web;

import lombok.experimental.UtilityClass;
import org.example.restapiprojectrepository.web.dto.MediaResponse;

import java.time.LocalDate;

@UtilityClass
public class TestBuilder {
    static MediaResponse mockMediaResponse() {
        return MediaResponse.builder()
                .title("testA")
                .mediaId(12L)
                .release_date(LocalDate.now().toString())
                .poster_path("random")
                .overview("overview")
                .build();
    }
}
