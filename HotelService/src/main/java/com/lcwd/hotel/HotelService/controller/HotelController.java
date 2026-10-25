package com.lcwd.hotel.HotelService.controller;

import com.lcwd.hotel.HotelService.entities.Hotel;
import com.lcwd.hotel.HotelService.services.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping("/hotels")
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody Hotel hotel){

        Hotel createdHotel = hotelService.createHotel(hotel);
        return new ResponseEntity<Hotel>(createdHotel, HttpStatus.CREATED);
    }


    @GetMapping({"/hotels/{hotelId}", "/hotel/{hotelId}"})
    public ResponseEntity<Hotel> getSingleHotel(@PathVariable  String hotelId){

        Hotel hotel = hotelService.getSpecificHotelById(hotelId);
        return new ResponseEntity<>(hotel, HttpStatus.OK);

    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> getAllHotels(){

        List<Hotel> hotelList = hotelService.getAllHotels();
        return new ResponseEntity<>(hotelList, HttpStatus.OK);
    }
}
