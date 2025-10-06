package com.omdb.movie.JavengersCinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbFindByIdResponse {
    @JsonProperty("movie_results")
    private List<TmdbFindMovieResult> movieResults;
    // Other result types (person_results, tv_results, etc.) can be added if needed
}
