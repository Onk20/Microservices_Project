package com.lcwd.user.service.external.services;

import com.lcwd.user.service.entities.Rating;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(name = "RATINGSERVICE")

public interface RatingService {

    @GetMapping("/ratings/user/{userId}")
    List<Rating> getRatingsByUserId(@PathVariable("userId") String userId);

    @PostMapping("/ratings")
    public Rating createRating(@RequestBody Rating values);

}
