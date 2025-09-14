package com.omdb.movie.JavengersCinema.controller;

import com.omdb.movie.JavengersCinema.dto.ApiResponse;
import com.omdb.movie.JavengersCinema.dto.GenreRequest;
import com.omdb.movie.JavengersCinema.dto.GenreResponse;
import com.omdb.movie.JavengersCinema.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @PostMapping
    public ResponseEntity<ApiResponse<GenreResponse>> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        GenreResponse createdGenre = genreService.createGenre(genreRequest);
        return new ResponseEntity<>(ApiResponse.success("Genre created successfully", createdGenre), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> getGenreById(@PathVariable Long id) {
        GenreResponse genreResponse = genreService.getGenreById(id);
        return new ResponseEntity<>(ApiResponse.success("Genre fetched successfully", genreResponse), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAllGenres() {
        List<GenreResponse> genres = genreService.getAllGenres();
        return new ResponseEntity<>(ApiResponse.success("Genres fetched successfully", genres), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResponse>> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest genreRequest) {
        GenreResponse updatedGenre = genreService.updateGenre(id, genreRequest);
        return new ResponseEntity<>(ApiResponse.success("Genre updated successfully", updatedGenre), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return new ResponseEntity<>(ApiResponse.success("Genre deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}
