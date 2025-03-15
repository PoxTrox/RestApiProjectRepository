package org.example.restapiprojectrepository.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Data
@Setter

public class MediaResponseWrapper {

    private List<MediaResponse> results;


}
