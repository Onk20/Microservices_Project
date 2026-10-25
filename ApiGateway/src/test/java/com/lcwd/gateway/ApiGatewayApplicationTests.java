package com.lcwd.gateway;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = "eureka.client.enabled=false")
class ApiGatewayApplicationTests {
	private static final HttpServer FIRST = stub("first");
	private static final HttpServer SECOND = stub("second");
	@Autowired private WebTestClient client;

	private static HttpServer stub(String name) {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
			server.createContext("/", exchange -> {
				byte[] body = name.getBytes(StandardCharsets.UTF_8);
				exchange.sendResponseHeaders(200, body.length);
				try (var output = exchange.getResponseBody()) {
					output.write(body);
				}
			});
			server.start();
			return server;
		} catch (IOException exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	@DynamicPropertySource
	static void discoveryInstances(DynamicPropertyRegistry properties) {
		for (String service : new String[]{"USERSERVICE", "HOTELSERVICE"}) {
			properties.add("spring.cloud.discovery.client.simple.instances." + service + "[0].uri",
					() -> "http://127.0.0.1:" + FIRST.getAddress().getPort());
			properties.add("spring.cloud.discovery.client.simple.instances." + service + "[1].uri",
					() -> "http://127.0.0.1:" + SECOND.getAddress().getPort());
		}
	}

	@AfterAll
	static void stopStubs() {
		FIRST.stop(0);
		SECOND.stop(0);
	}

	@Test
	void balancesHotelRequestsAcrossDiscoveredInstances() {
		Set<String> responses = new HashSet<>();
		for (int request = 0; request < 4; request++) {
			responses.add(client.get().uri("/hotels/example").exchange().expectStatus().isOk()
					.expectBody(String.class).returnResult().getResponseBody());
		}
		assertThat(responses).containsExactlyInAnyOrder("first", "second");
	}

	@Test
	void routesUsersAndLegacyHotelPath() {
		client.get().uri("/users/example").exchange().expectStatus().isOk();
		client.get().uri("/hotel/example").exchange().expectStatus().isOk();
	}

	@Test
	void returns503WhenNoRatingInstanceIsDiscovered() {
		client.get().uri("/ratings").exchange().expectStatus().isEqualTo(503);
	}

	@Test
	void unknownRoutesReturn404() {
		client.get().uri("/internal/not-a-route").exchange().expectStatus().isNotFound();
	}

	@Test
	void healthEndpointIsAccessible() {
		client.get().uri("/actuator/health").exchange().expectStatus().isOk();
	}
}
