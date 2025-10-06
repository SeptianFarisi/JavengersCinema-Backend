package com.omdb.movie.JavengersCinema.service;

import com.omdb.movie.JavengersCinema.dto.ApiResponse;
import com.omdb.movie.JavengersCinema.dto.MovieRequest;
import com.omdb.movie.JavengersCinema.dto.MovieResponse;
import com.omdb.movie.JavengersCinema.dto.MovieSearchRequest;
import com.omdb.movie.JavengersCinema.dto.OmdbMovieDetailDto;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchDto;
import com.omdb.movie.JavengersCinema.dto.OmdbSearchResponse;
import com.omdb.movie.JavengersCinema.dto.TmdbFindByIdResponse;
import com.omdb.movie.JavengersCinema.dto.TmdbMovieVideosResponse;
import com.omdb.movie.JavengersCinema.dto.TmdbVideoResult;
import com.omdb.movie.JavengersCinema.dto.TrailerRequest;
import com.omdb.movie.JavengersCinema.dto.TrailerResponse;
import com.omdb.movie.JavengersCinema.exception.ResourceNotFoundException;
import com.omdb.movie.JavengersCinema.model.MovieDetail;
import com.omdb.movie.JavengersCinema.model.Trailer;
import com.omdb.movie.JavengersCinema.repository.MovieRepository;
import com.omdb.movie.JavengersCinema.repository.TrailerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;
    private final TrailerRepository trailerRepository;
    private static final String OMDB_API_BASE_URL = "http://www.omdbapi.com/";
    private static final String TMDB_API_BASE_URL = "https://api.themoviedb.org/3";

    public MovieServiceImpl(RestTemplate restTemplate, MovieRepository movieRepository, TrailerRepository trailerRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
        this.trailerRepository = trailerRepository;
    }

    // OMDb API operations
    @Override
    public ApiResponse<OmdbSearchResponse> searchMoviesFromOmdb(MovieSearchRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(OMDB_API_BASE_URL)
                .queryParam("apikey", omdbApiKey)
                .queryParam("s", request.getTitle());

        if (request.getYear() != null && !request.getYear().isEmpty()) {
            uriBuilder.queryParam("y", request.getYear());
        }
        if (request.getType() != null && !request.getType().isEmpty()) {
            uriBuilder.queryParam("type", request.getType());
        }
        if (request.getPage() != null) {
            uriBuilder.queryParam("page", request.getPage());
        }

        try {
            OmdbSearchResponse response = restTemplate.getForObject(uriBuilder.toUriString(), OmdbSearchResponse.class);
            if (response != null && "True".equals(response.getResponse())) {
                return ApiResponse.success("Movies fetched successfully from OMDb", response);
            } else {
                return ApiResponse.error(response != null ? response.getError() : "No movies found from OMDb or an error occurred.");
            }
        } catch (Exception e) {
            return ApiResponse.error("Error fetching movies from OMDb: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<OmdbMovieDetailDto> getMovieDetailOmdbById(String imdbId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(OMDB_API_BASE_URL)
                .queryParam("apikey", omdbApiKey)
                .queryParam("i", imdbId);

        try {
            OmdbMovieDetailDto response = restTemplate.getForObject(uriBuilder.toUriString(), OmdbMovieDetailDto.class);
            if (response != null && "True".equals(response.getResponse())) { // OMDb returns "Response":"True" if found
                return ApiResponse.success("Movie fetched successfully from OMDb", response);
            } else {
                return ApiResponse.error(response != null ? response.getError() : "Movie not found from OMDb for IMDb ID: " + imdbId);
            }
        } catch (Exception e) {
            return ApiResponse.error("Error fetching movie from OMDb by ID: " + e.getMessage());
        }
    }

    // Local database CRUD operations
    @Override
    public ApiResponse<MovieResponse> saveMovie(MovieRequest movieRequest) {
        MovieDetail movie = new MovieDetail();
        BeanUtils.copyProperties(movieRequest, movie);

        // If imdbID is provided but other details are missing, try to fetch from OMDb
        if (movieRequest.getImdbID() != null && movieRequest.getTitle() == null) {
            ApiResponse<OmdbMovieDetailDto> omdbResponse = getMovieDetailOmdbById(movieRequest.getImdbID());
            if (omdbResponse.isSuccess()) {
                OmdbMovieDetailDto omdbMovie = omdbResponse.getData();
                // Manually copy properties that exist in MovieDetail and OmdbMovieDetailDto
                movie.setImdbID(omdbMovie.getImdbID());
                movie.setTitle(omdbMovie.getTitle());
                try {
                    movie.setYear(Integer.parseInt(omdbMovie.getYear()));
                } catch (NumberFormatException e) {
                    movie.setYear(null); // Handle cases where year is not a valid integer
                }
                movie.setType(omdbMovie.getType());
                movie.setPoster(omdbMovie.getPoster());
                movie.setPlot(omdbMovie.getPlot());
                movie.setDirector(omdbMovie.getDirector());
                movie.setActors(omdbMovie.getActors());
                movie.setRuntime(omdbMovie.getRuntime());
                movie.setImdbRating(omdbMovie.getImdbRating());
            } else {
                return ApiResponse.error("Failed to fetch movie details from OMDb for IMDb ID: " + movieRequest.getImdbID());
            }
        }

        MovieDetail savedMovie = movieRepository.save(movie);
        MovieResponse movieResponse = new MovieResponse();
        BeanUtils.copyProperties(savedMovie, movieResponse);
        trailerRepository.findByMovieId(savedMovie.getId()).ifPresent(trailer -> movieResponse.setTrailer(trailer.getTrailerUrl()));
        return ApiResponse.success("Movie saved successfully", movieResponse);
    }

    @Override
    public ApiResponse<MovieResponse> getMovieById(Long id) {
        MovieDetail movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));
        MovieResponse movieResponse = new MovieResponse();
        BeanUtils.copyProperties(movie, movieResponse);
        trailerRepository.findByMovieId(movie.getId()).ifPresent(trailer -> movieResponse.setTrailer(trailer.getTrailerUrl()));
        return ApiResponse.success("Movie fetched successfully", movieResponse);
    }

    @Override
    public ApiResponse<MovieResponse> getMovieByImdbIdOrTitle(String identifier) {
        // 1. Try to find in local database by IMDb ID
        Optional<MovieDetail> movieByImdbId = movieRepository.findByImdbID(identifier);
        if (movieByImdbId.isPresent()) {
            MovieResponse response = new MovieResponse();
            BeanUtils.copyProperties(movieByImdbId.get(), response);
            trailerRepository.findByMovieId(movieByImdbId.get().getId()).ifPresent(trailer -> response.setTrailer(trailer.getTrailerUrl()));
            return ApiResponse.success("Movie fetched from local database by IMDb ID", response);
        }

        // 2. Try to find in local database by title (simple search, could be improved)
        // For simplicity, we'll assume 'identifier' could be a title if not an IMDb ID
        // A more robust solution would involve a 'findByTitleContainingIgnoreCase' in repository
        List<MovieDetail> moviesByTitle = movieRepository.findAll().stream()
                .filter(movie -> movie.getTitle() != null && movie.getTitle().equalsIgnoreCase(identifier))
                .collect(Collectors.toList());

        if (!moviesByTitle.isEmpty()) {
            // Return the first match for simplicity, or handle multiple matches
            MovieResponse response = new MovieResponse();
            BeanUtils.copyProperties(moviesByTitle.get(0), response);
            trailerRepository.findByMovieId(moviesByTitle.get(0).getId()).ifPresent(trailer -> response.setTrailer(trailer.getTrailerUrl()));
            return ApiResponse.success("Movie fetched from local database by title", response);
        }

        // 3. If not found locally, fetch from OMDb by IMDb ID or title
        ApiResponse<OmdbMovieDetailDto> omdbResponse = getMovieDetailOmdbById(identifier); // Try as IMDb ID first
        if (!omdbResponse.isSuccess()) {
            // If not found by IMDb ID, try searching by title
            MovieSearchRequest searchRequest = new MovieSearchRequest(identifier, null, null, 1);
            ApiResponse<OmdbSearchResponse> omdbSearchResponse = searchMoviesFromOmdb(searchRequest);

            if (omdbSearchResponse.isSuccess() && omdbSearchResponse.getData().getSearch() != null && !omdbSearchResponse.getData().getSearch().isEmpty()) {
                // Take the first result from OMDb search
                OmdbSearchDto firstOmdbMovie = omdbSearchResponse.getData().getSearch().get(0);
                omdbResponse = getMovieDetailOmdbById(firstOmdbMovie.getImdbID()); // Get full details
            }
        }

        if (omdbResponse.isSuccess()) {
            OmdbMovieDetailDto omdbMovie = omdbResponse.getData();
            MovieDetail newMovie = new MovieDetail();
            // Manually copy properties that exist in MovieDetail and OmdbMovieDetailDto
            newMovie.setImdbID(omdbMovie.getImdbID());
            newMovie.setTitle(omdbMovie.getTitle());
            try {
                newMovie.setYear(Integer.parseInt(omdbMovie.getYear()));
                } catch (NumberFormatException e) {
                newMovie.setYear(null); // Handle cases where year is not a valid integer
            }
            newMovie.setType(omdbMovie.getType());
            newMovie.setPoster(omdbMovie.getPoster());
            newMovie.setPlot(omdbMovie.getPlot());
            newMovie.setDirector(omdbMovie.getDirector());
            newMovie.setActors(omdbMovie.getActors());
            newMovie.setRuntime(omdbMovie.getRuntime());
            newMovie.setImdbRating(omdbMovie.getImdbRating());

            MovieDetail savedMovie = movieRepository.save(newMovie);
            MovieResponse movieResponse = new MovieResponse();
            BeanUtils.copyProperties(savedMovie, movieResponse);
            trailerRepository.findByMovieId(savedMovie.getId()).ifPresent(trailer -> movieResponse.setTrailer(trailer.getTrailerUrl()));
            return ApiResponse.success("Movie fetched from OMDb and saved to local database", movieResponse);
        } else {
            throw new ResourceNotFoundException("Movie not found by IMDb ID or title: " + identifier);
        }
    }

    @Override
    public ApiResponse<List<MovieResponse>> getAllMovies() {
        List<MovieDetail> movies = movieRepository.findAll();
        List<MovieResponse> movieResponses = movies.stream()
                .map(movie -> {
                    MovieResponse response = new MovieResponse();
                    BeanUtils.copyProperties(movie, response);
                    trailerRepository.findByMovieId(movie.getId()).ifPresent(trailer -> response.setTrailer(trailer.getTrailerUrl()));
                    return response;
                })
                .collect(Collectors.toList());
        return ApiResponse.success("Movies fetched successfully", movieResponses);
    }

    @Override
    public ApiResponse<MovieResponse> updateMovie(Long id, MovieRequest movieRequest) {
        MovieDetail existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));

        BeanUtils.copyProperties(movieRequest, existingMovie, "id"); // Exclude ID from copy
        MovieDetail updatedMovie = movieRepository.save(existingMovie);

        MovieResponse movieResponse = new MovieResponse();
        BeanUtils.copyProperties(updatedMovie, movieResponse);
        trailerRepository.findByMovieId(updatedMovie.getId()).ifPresent(trailer -> movieResponse.setTrailer(trailer.getTrailerUrl()));
        return ApiResponse.success("Movie updated successfully", movieResponse);
    }

    @Override
    public ApiResponse<Void> deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found with ID: " + id);
        }
        movieRepository.deleteById(id);
        return ApiResponse.success("Movie deleted successfully", null);
    }

    @Override
    public ApiResponse<TrailerResponse> createTrailer(TrailerRequest trailerRequest) {
        MovieDetail movie = movieRepository.findById(trailerRequest.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + trailerRequest.getMovieId()));

        if (trailerRepository.findByMovieId(movie.getId()).isPresent()) {
            return ApiResponse.error("Trailer already exists for movie with ID: " + movie.getId());
        }

        Trailer trailer = new Trailer();
        trailer.setTrailerUrl(trailerRequest.getTrailerUrl());
        trailer.setMovie(movie);
        Trailer savedTrailer = trailerRepository.save(trailer);

        TrailerResponse trailerResponse = new TrailerResponse();
        BeanUtils.copyProperties(savedTrailer, trailerResponse);
        trailerResponse.setMovieId(movie.getId());
        return ApiResponse.success("Trailer created successfully", trailerResponse);
    }

    @Override
    public ApiResponse<TrailerResponse> updateTrailer(Long trailerId, TrailerRequest trailerRequest) {
        Trailer existingTrailer = trailerRepository.findById(trailerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trailer not found with ID: " + trailerId));

        existingTrailer.setTrailerUrl(trailerRequest.getTrailerUrl());
        Trailer updatedTrailer = trailerRepository.save(existingTrailer);

        TrailerResponse trailerResponse = new TrailerResponse();
        BeanUtils.copyProperties(updatedTrailer, trailerResponse);
        trailerResponse.setMovieId(updatedTrailer.getMovie().getId());
        return ApiResponse.success("Trailer updated successfully", trailerResponse);
    }

    @Override
    public ApiResponse<TrailerResponse> getTrailerByMovieId(Long movieId) {
        Trailer trailer = trailerRepository.findByMovieId(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Trailer not found for movie with ID: " + movieId));

        TrailerResponse trailerResponse = new TrailerResponse();
        BeanUtils.copyProperties(trailer, trailerResponse);
        trailerResponse.setMovieId(movieId);
        return ApiResponse.success("Trailer fetched successfully", trailerResponse);
    }

    @Override
    public ApiResponse<Void> deleteTrailer(Long trailerId) {
        if (!trailerRepository.existsById(trailerId)) {
            throw new ResourceNotFoundException("Trailer not found with ID: " + trailerId);
        }
        trailerRepository.deleteById(trailerId);
        return ApiResponse.success("Trailer deleted successfully", null);
    }

    @Override
    public ApiResponse<Long> getTmdbMovieIdByImdbId(String imdbId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(TMDB_API_BASE_URL + "/find/" + imdbId)
                .queryParam("api_key", tmdbApiKey)
                .queryParam("external_source", "imdb_id");

        try {
            TmdbFindByIdResponse response = restTemplate.getForObject(uriBuilder.toUriString(), TmdbFindByIdResponse.class);
            if (response != null && response.getMovieResults() != null && !response.getMovieResults().isEmpty()) {
                return ApiResponse.success("TMDB Movie ID fetched successfully", response.getMovieResults().get(0).getId());
            } else {
                return ApiResponse.error("TMDB Movie ID not found for IMDb ID: " + imdbId);
            }
        } catch (Exception e) {
            return ApiResponse.error("Error fetching TMDB Movie ID: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<TrailerResponse>> getTrailersFromTmdb(Long tmdbMovieId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(TMDB_API_BASE_URL + "/movie/" + tmdbMovieId + "/videos")
                .queryParam("api_key", tmdbApiKey);

        try {
            TmdbMovieVideosResponse response = restTemplate.getForObject(uriBuilder.toUriString(), TmdbMovieVideosResponse.class);
            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                List<TrailerResponse> trailerResponses = response.getResults().stream()
                        .filter(video -> "Trailer".equalsIgnoreCase(video.getType()) && "YouTube".equalsIgnoreCase(video.getSite()))
                        .map(video -> new TrailerResponse(null, video.getName(), "https://www.youtube.com/watch?v=" + video.getKey(), null))
                        .collect(Collectors.toList());
                return ApiResponse.success("Trailers fetched successfully from TMDB", trailerResponses);
            } else {
                return ApiResponse.error("No trailers found from TMDB for movie ID: " + tmdbMovieId);
            }
        } catch (Exception e) {
            return ApiResponse.error("Error fetching trailers from TMDB: " + e.getMessage());
        }
    }
}
