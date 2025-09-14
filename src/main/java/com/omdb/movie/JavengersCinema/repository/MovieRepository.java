package com.omdb.movie.JavengersCinema.repository;

import com.omdb.movie.JavengersCinema.model.MovieDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieDetail, Long> {
    Optional<MovieDetail> findByImdbID(String imdbID);
}
