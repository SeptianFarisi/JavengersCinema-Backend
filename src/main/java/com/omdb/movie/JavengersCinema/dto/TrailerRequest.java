package com.omdb.movie.JavengersCinema.dto;

import lombok.Data;

@Data
public class TrailerRequest {
    private String trailerUrl;
    private Long movieId;
}
