package com.omdb.movie.JavengersCinema.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmdbVideoResult {
    @JsonProperty("iso_639_1")
    private String iso6391;
    @JsonProperty("iso_3166_1")
    private String iso31661;
    private String name;
    private String key;
    private String site;
    private Integer size;
    private String type;
    private Boolean official;
    @JsonProperty("published_at")
    private String publishedAt;
    private String id;
}
