package com.omdb.movie.JavengersCinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchRequest {
    private String title;
    private String year;
    private String type; // movie, series, episode
    private Integer page; // For pagination
}
