package com.omdb.movie.JavengersCinema.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GenreRequest {
    @NotEmpty(message = "Genre names cannot be empty")
    @Size(min = 1, message = "At least one genre name is required")
    private List<String> names;
}
