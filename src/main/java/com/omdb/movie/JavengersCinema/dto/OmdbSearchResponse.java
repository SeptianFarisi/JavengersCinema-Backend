package com.omdb.movie.JavengersCinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmdbSearchResponse {
    @JsonProperty("Search")
    private List<OmdbSearchDto> search;
    @JsonProperty("totalResults")
    private String totalResults;
    @JsonProperty("Response")
    private String response; // "True" or "False"
    @JsonProperty("Error")
    private String error; // Error message if Response is "False"
}
