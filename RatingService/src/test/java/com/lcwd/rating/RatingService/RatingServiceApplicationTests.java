package com.lcwd.rating.RatingService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.lcwd.rating.RatingService.repositories.RatingRepository;
import com.lcwd.rating.RatingService.entities.Rating;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class RatingServiceApplicationTests {
	@Autowired private MockMvc mvc;
	@MockitoBean private RatingRepository repository;

	@Test
	void contextLoads() {
	}

	@Test
	void createsRatingWithGeneratedId() throws Exception {
		when(repository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
		mvc.perform(post("/ratings").contentType(MediaType.APPLICATION_JSON)
						.content("{\"userId\":\"user-1\",\"hotelId\":\"hotel-1\",\"rating\":9}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.ratingId").isNotEmpty());
	}

	@Test
	void rejectsOutOfRangeRating() throws Exception {
		mvc.perform(post("/ratings").contentType(MediaType.APPLICATION_JSON)
						.content("{\"userId\":\"user-1\",\"hotelId\":\"hotel-1\",\"rating\":11}"))
				.andExpect(status().isBadRequest());
		verify(repository, never()).save(any());
	}

	@Test
	void filtersByUserAndHotel() throws Exception {
		Rating rating = new Rating("rating-1", "user-1", "hotel-1", 9, "Good");
		when(repository.findByUserId("user-1")).thenReturn(List.of(rating));
		when(repository.findByHotelId("hotel-1")).thenReturn(List.of(rating));
		mvc.perform(get("/ratings/user/user-1")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].hotelId").value("hotel-1"));
		mvc.perform(get("/ratings/hotel/hotel-1")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].userId").value("user-1"));
	}
}
