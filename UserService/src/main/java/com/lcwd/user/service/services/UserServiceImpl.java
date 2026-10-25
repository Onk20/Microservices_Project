package com.lcwd.user.service.services;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResouceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.external.services.RatingService;
import com.lcwd.user.service.repositories.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingService ratingService;


    @Autowired
    private HotelService hotelService;

    @Override
    public User saveUser(User user) {

        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    @Override
    public List<User> getAllUser() {

        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }

    @Override
    @CircuitBreaker(name = "ratingHotelBreaker")
    public User getUserWithUserId(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResouceNotFoundException("User not found with User id :"+userId));

        List<Rating> ratings = ratingService.getRatingsByUserId(user.getUserId());
        List<Rating> ratingList = ratings.stream().map(rating -> {
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
            rating.setHotel(hotel);
            return rating;
        }).toList();

        user.setRatings(ratingList);

        return user;
    }
}
