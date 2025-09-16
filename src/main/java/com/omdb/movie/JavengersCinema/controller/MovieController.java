package com.omdb.movie.JavengersCinema.controller;

import com.omdb.movie.JavengersCinema.dto.ApiResponse;
import com.omdb.movie.JavengersCinema.dto.MovieRequest;
import com.omdb.movie.JavengersCinema.dto.MovieResponse;
import com.omdb.movie.JavengersCinema.dto.MovieSearchRequest;
import com.omdb.movie.JavengersCinema.dto.OmdbMovieDetailDto;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchDto;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchResponse;
import com.omdb.movie.JavengersCinema.dto.TrailerRequest;
import com.omdb.movie.JavengersCinema.dto.TrailerResponse;
import com.omdb.movie.JavengersCinema.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // OMDb API Endpoints
    @GetMapping("/omdb/search")
    public ResponseEntity<ApiResponse<OmdbSearchResponse>> searchMoviesFromOmdb(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        MovieSearchRequest request = new MovieSearchRequest(title, year, type, page);
        ApiResponse<OmdbSearchResponse> response = movieService.searchMoviesFromOmdb(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/omdb/{imdbId}")
    public ResponseEntity<ApiResponse<OmdbMovieDetailDto>> getMovieFromOmdbById(@PathVariable String imdbId) {
        ApiResponse<OmdbMovieDetailDto> response = movieService.getMovieDetailOmdbById(imdbId);
        return ResponseEntity.ok(response);
    }

    // Local Database CRUD Endpoints
    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@RequestBody MovieRequest movieRequest) {
        ApiResponse<MovieResponse> response = movieService.saveMovie(movieRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/local/{id}") // Endpoint to get movie from local DB by its primary key ID
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieFromLocalById(@PathVariable Long id) {
        ApiResponse<MovieResponse> response = movieService.getMovieById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search") // Endpoint with fallback logic
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieByImdbIdOrTitle(@RequestParam String identifier) {
        ApiResponse<MovieResponse> response = movieService.getMovieByImdbIdOrTitle(identifier);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies() {
        ApiResponse<List<MovieResponse>> response = movieService.getAllMovies();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(@PathVariable Long id, @RequestBody MovieRequest movieRequest) {
        ApiResponse<MovieResponse> response = movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        ApiResponse<Void> response = movieService.deleteMovie(id);
        return ResponseEntity.ok(response);
    }
}
