package org.example.restapiprojectrepository.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaRequest {

    private Long id;

    private String title;

    private String overview;

    private String release_date;

    private String poster_path;


}
