package com.omdb.movie.JavengersCinema.dto;

import lombok.Data;

@Data
public class TrailerResponse {
    private Long id;
    private String trailerUrl;
    private Long movieId;
}
