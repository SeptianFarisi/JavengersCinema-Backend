package com.omdb.movie.JavengersCinema.service;

import com.omdb.movie.JavengersCinema.dto.GenreRequest;
import com.omdb.movie.JavengersCinema.dto.GenreResponse;
import com.omdb.movie.JavengersCinema.exception.ResourceNotFoundException;
import com.omdb.movie.JavengersCinema.model.Genre;
import com.omdb.movie.JavengersCinema.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public GenreResponse createGenre(GenreRequest genreRequest) {
        // Assuming genreRequest.getNames() contains a list of genre names to be created
        // This method will create multiple genres if provided, or a single one.
        // The task description implies CRUD for a single genre, but the GenreRequest DTO
        // was modified to accept a list of names. I will implement it to handle a single genre creation
        // for now, and if the user wants to create multiple genres in one go, we can adjust.
        // For now, I'll take the first name in the list.
        String genreName = genreRequest.getNames().get(0);
        Genre genre = new Genre();
        genre.setName(genreName);
        Genre savedGenre = genreRepository.save(genre);
        return convertToDto(savedGenre);
    }

    @Override
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id " + id));
        return convertToDto(genre);
    }

    @Override
    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GenreResponse updateGenre(Long id, GenreRequest genreRequest) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id " + id));
        genre.setName(genreRequest.getNames().get(0)); // Assuming single genre update
        Genre updatedGenre = genreRepository.save(genre);
        return convertToDto(updatedGenre);
    }

    @Override
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre not found with id " + id);
        }
        genreRepository.deleteById(id);
    }

    private GenreResponse convertToDto(Genre genre) {
        GenreResponse genreResponse = new GenreResponse();
        genreResponse.setId(genre.getId());
        genreResponse.setName(genre.getName());
        return genreResponse;
    }
}
