package com.lcwd.hotel.HotelService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcwd.hotel.HotelService.entities.Hotel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
	"eureka.client.enabled=false", "spring.datasource.url=jdbc:h2:mem:hotels;MODE=PostgreSQL",
	"spring.datasource.username=sa", "spring.datasource.password=",
	"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class HotelServiceApplicationTests {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

	@Test
	void contextLoads() {
	}

    @Test
    void createsAndFetchesHotelOnBothPaths() throws Exception {
	String response = mvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\":\"Harbor Hotel\",\"location\":\"Mumbai\",\"about\":\"Seafront\"}"))
		.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
	Hotel hotel = mapper.readValue(response, Hotel.class);
	mvc.perform(get("/hotels/{id}", hotel.getHotelId())).andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value("Harbor Hotel"));
	mvc.perform(get("/hotel/{id}", hotel.getHotelId())).andExpect(status().isOk());
    }

    @Test
    void rejectsInvalidHotel() throws Exception {
	mvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("{}"))
		.andExpect(status().isBadRequest());
    }

    @Test
    void missingHotelReturns404() throws Exception {
	mvc.perform(get("/hotels/missing")).andExpect(status().isNotFound())
		.andExpect(jsonPath("$.success").value(false));
    }
}
