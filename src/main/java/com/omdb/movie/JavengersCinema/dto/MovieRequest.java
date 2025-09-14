package com.omdb.movie.JavengersCinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {
    private String imdbID;
    private String title;
    private String year;
    private String type;
    private String poster;
    private String plot;
    private String genre;
    private String director;
    private String imdbRating;
    private String trailer; // New field for trailer URL
}
