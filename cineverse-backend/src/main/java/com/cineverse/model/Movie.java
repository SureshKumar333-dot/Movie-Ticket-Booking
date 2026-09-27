package com.cineverse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false)
    private String title;

    private String language;
    private String genre;
    private String duration;
    private String rating;
    private String badge;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cast_members", columnDefinition = "TEXT")
    private String castMembers;

    private String director;
    private String poster;

    @Column(name = "price_standard")
    private Integer priceStandard;

    @Column(name = "price_premium")
    private Integer pricePremium;

    public Movie() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCastMembers() { return castMembers; }
    public void setCastMembers(String castMembers) { this.castMembers = castMembers; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public Integer getPriceStandard() { return priceStandard; }
    public void setPriceStandard(Integer priceStandard) { this.priceStandard = priceStandard; }

    public Integer getPricePremium() { return pricePremium; }
    public void setPricePremium(Integer pricePremium) { this.pricePremium = pricePremium; }
}
