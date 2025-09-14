package com.omdb.movie.JavengersCinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie_detail", indexes = {
    @Index(name = "idx_year", columnList = "year")
})
public class MovieDetail {

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imdbID;
    private String title;
    @Column(name = "year")
    private Integer year;
    private String type;
    private String poster;
    private String plot;
    private String director;
    private String actors;
    private String runtime;
    private String imdbRating;
}
