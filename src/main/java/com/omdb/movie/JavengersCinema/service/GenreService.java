package com.omdb.movie.JavengersCinema.service;

import com.omdb.movie.JavengersCinema.dto.GenreRequest;
import com.omdb.movie.JavengersCinema.dto.GenreResponse;

import java.util.List;

public interface GenreService {
    GenreResponse createGenre(GenreRequest genreRequest);
    GenreResponse getGenreById(Long id);
    List<GenreResponse> getAllGenres();
    GenreResponse updateGenre(Long id, GenreRequest genreRequest);
    void deleteGenre(Long id);
}
