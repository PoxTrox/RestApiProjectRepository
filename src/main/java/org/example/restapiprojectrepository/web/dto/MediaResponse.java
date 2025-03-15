package org.example.restapiprojectrepository.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaResponse {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("overview")
    private String overview;
    @JsonProperty("release_date")
    private String release_date;
    @JsonProperty("poster_path")
    private String poster_path;

}
