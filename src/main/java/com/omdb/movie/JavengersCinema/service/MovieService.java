package com.omdb.movie.JavengersCinema.service;

import com.omdb.movie.JavengersCinema.dto.ApiResponse;
import com.omdb.movie.JavengersCinema.dto.MovieSearchRequest;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchDto;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchResponse;
import com.omdb.movie.JavengersCinema.dto.MovieRequest;
import com.omdb.movie.JavengersCinema.dto.MovieResponse;
import com.omdb.movie.JavengersCinema.dto.TrailerRequest;
import com.omdb.movie.JavengersCinema.dto.TrailerResponse;

import java.util.List;

public interface MovieService {
    // OMDb API operations
    ApiResponse<OmdbSearchResponse> searchMoviesFromOmdb(MovieSearchRequest request);
    ApiResponse<OmdbSearchDto> getMovieFromOmdbById(String imdbId);

    // Local database CRUD operations
    ApiResponse<MovieResponse> saveMovie(MovieRequest movieRequest);
    ApiResponse<MovieResponse> getMovieById(Long id);
    ApiResponse<MovieResponse> getMovieByImdbIdOrTitle(String identifier); // New method for fallback logic
    ApiResponse<List<MovieResponse>> getAllMovies();
    ApiResponse<MovieResponse> updateMovie(Long id, MovieRequest movieRequest);
    ApiResponse<Void> deleteMovie(Long id);

    // Trailer CRUD operations
    ApiResponse<TrailerResponse> createTrailer(TrailerRequest trailerRequest);
    ApiResponse<TrailerResponse> updateTrailer(Long trailerId, TrailerRequest trailerRequest);
    ApiResponse<TrailerResponse> getTrailerByMovieId(Long movieId);
    ApiResponse<Void> deleteTrailer(Long trailerId);
}
