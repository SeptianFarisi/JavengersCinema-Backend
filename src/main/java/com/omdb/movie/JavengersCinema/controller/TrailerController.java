package com.omdb.movie.JavengersCinema.controller;

import com.omdb.movie.JavengersCinema.dto.ApiResponse;
import com.omdb.movie.JavengersCinema.dto.TrailerRequest;
import com.omdb.movie.JavengersCinema.dto.TrailerResponse;
import com.omdb.movie.JavengersCinema.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trailers")
public class TrailerController {

    private final MovieService movieService;

    public TrailerController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrailerResponse>> createTrailer(@RequestBody TrailerRequest trailerRequest) {
        ApiResponse<TrailerResponse> response = movieService.createTrailer(trailerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{trailerId}")
    public ResponseEntity<ApiResponse<TrailerResponse>> updateTrailer(
            @PathVariable Long trailerId,
            @RequestBody TrailerRequest trailerRequest) {
        ApiResponse<TrailerResponse> response = movieService.updateTrailer(trailerId, trailerRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<TrailerResponse>> getTrailerByMovieId(@PathVariable Long movieId) {
        ApiResponse<TrailerResponse> response = movieService.getTrailerByMovieId(movieId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trailerId}")
    public ResponseEntity<ApiResponse<Void>> deleteTrailer(@PathVariable Long trailerId) {
        ApiResponse<Void> response = movieService.deleteTrailer(trailerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tmdb/{imdbId}")
    public ResponseEntity<ApiResponse<List<TrailerResponse>>> getTrailersFromTmdb(@PathVariable String imdbId) {
        ApiResponse<Long> tmdbIdResponse = movieService.getTmdbMovieIdByImdbId(imdbId);
        if (!tmdbIdResponse.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.error(tmdbIdResponse.getMessage()), HttpStatus.NOT_FOUND);
        }
        Long tmdbMovieId = tmdbIdResponse.getData();
        ApiResponse<List<TrailerResponse>> trailersResponse = movieService.getTrailersFromTmdb(tmdbMovieId);
        return ResponseEntity.ok(trailersResponse);
    }
}
