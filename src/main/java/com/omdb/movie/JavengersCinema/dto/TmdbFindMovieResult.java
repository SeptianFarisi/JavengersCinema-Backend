package com.omdb.movie.JavengersCinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbFindMovieResult {
    private Long id;
    // Only need id for now, other fields can be added if needed later
}
