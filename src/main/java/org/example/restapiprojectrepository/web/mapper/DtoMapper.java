package org.example.restapiprojectrepository.web.mapper;

import lombok.experimental.UtilityClass;
import org.example.restapiprojectrepository.model.Media;
import org.example.restapiprojectrepository.web.dto.MediaResponse;

@UtilityClass
public class DtoMapper {

    public static MediaResponse mediaResponseDtoToMediaResponse(Media media) {

        return MediaResponse.builder()
                .title(media.getTitle())
                .release_date(media.getReleaseDate())
                .overview(media.getOverview())
                .release_date(media.getReleaseDate())
                .poster_path(media.getPosterPath())

                .build();
    }
}
