package com.lcwd.rating.RatingService.controllers;

import com.lcwd.rating.RatingService.entities.Rating;
import com.lcwd.rating.RatingService.services.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping("/ratings")
    public ResponseEntity<Rating> createRating(@Valid @RequestBody Rating rating){

        Rating createdRating = ratingService.createRating(rating);

        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    @GetMapping("/ratings")
    public ResponseEntity<List<Rating>> getRatings(){
        return ResponseEntity.ok(ratingService.getAllRatings());
    }

    @GetMapping("/ratings/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUserId(@PathVariable String userId){
        return ResponseEntity.ok(ratingService.getAllRatingsByUserId(userId));
    }

    @GetMapping("/ratings/hotel/{hotelId}")
    public ResponseEntity<List<Rating>> getRatingsByHotelId(@PathVariable String hotelId){
        return ResponseEntity.ok(ratingService.getAllRatingsByHotel(hotelId));
    }



}
