package com.omdb.movie.JavengersCinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovieVideosResponse {
    private Long id;
    private List<TmdbVideoResult> results;
}
