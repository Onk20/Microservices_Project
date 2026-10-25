package com.lcwd.user.service;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.external.services.RatingService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:users;MODE=MySQL",
		"spring.datasource.username=sa", "spring.datasource.password=",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class UserServiceApplicationTests {
	@Autowired private UserService userService;
	@Autowired private UserRepository userRepository;
	@Autowired private MockMvc mvc;
	@Autowired private CircuitBreakerRegistry breakers;
	@MockitoBean private RatingService ratingService;
	@MockitoBean private HotelService hotelService;

	@BeforeEach
	void resetState() {
		userRepository.deleteAll();
		breakers.circuitBreaker("ratingHotelBreaker").reset();
	}

	@Test
	void aggregatesRatingsAndHotels() {
		User user = userService.saveUser(User.builder().name("Ada").email("ada@example.com").build());
		Rating rating = Rating.builder().hotelId("hotel-1").rating(9).build();
		Hotel hotel = new Hotel();
		hotel.setHotelId("hotel-1");
		when(ratingService.getRatingsByUserId(user.getUserId())).thenReturn(List.of(rating));
		when(hotelService.getHotel("hotel-1")).thenReturn(hotel);

		User result = userService.getUserWithUserId(user.getUserId());

		assertThat(result.getRatings()).hasSize(1);
		assertThat(result.getRatings().get(0).getHotel().getHotelId()).isEqualTo("hotel-1");
	}

	@Test
	void handlesUsersWithoutRatings() {
		User user = userService.saveUser(User.builder().name("Ada").email("ada@example.com").build());
		when(ratingService.getRatingsByUserId(user.getUserId())).thenReturn(List.of());
		assertThat(userService.getUserWithUserId(user.getUserId()).getRatings()).isEmpty();
		verifyNoInteractions(hotelService);
	}

	@Test
	void missingUserReturns404WithoutCallingDependencies() throws Exception {
		mvc.perform(get("/users/missing"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.success").value(false));
		verifyNoInteractions(ratingService, hotelService);
		assertThat(breakers.circuitBreaker("ratingHotelBreaker").getMetrics().getNumberOfFailedCalls()).isZero();
	}

	@Test
	void dependencyFailureReturns503NotDummyUser() throws Exception {
		User user = userService.saveUser(User.builder().name("Ada").email("ada@example.com").build());
		when(ratingService.getRatingsByUserId(anyString())).thenThrow(mock(FeignException.ServiceUnavailable.class));
		mvc.perform(get("/users/{id}", user.getUserId()))
				.andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.userId").doesNotExist());
	}

	@Test
	void openCircuitReturns503WithoutCallingDependencies() throws Exception {
		breakers.circuitBreaker("ratingHotelBreaker").transitionToOpenState();
		mvc.perform(get("/users/any"))
				.andExpect(status().isServiceUnavailable());
		verifyNoInteractions(ratingService, hotelService);
	}

	@Test
	void rejectsInvalidUser() throws Exception {
		mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/users")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}
}
