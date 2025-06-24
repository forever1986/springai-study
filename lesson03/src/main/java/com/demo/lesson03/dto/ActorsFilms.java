package com.demo.lesson03.dto;

import java.util.List;

public class ActorsFilms {

    private String actor;
    private List<String> movies;

    public ActorsFilms() {
    }

    public ActorsFilms(String actor, List<String> movies) {
        this.actor = actor;
        this.movies = movies;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public List<String> getMovies() {
        return movies;
    }

    public void setMovies(List<String> movies) {
        this.movies = movies;
    }
}
