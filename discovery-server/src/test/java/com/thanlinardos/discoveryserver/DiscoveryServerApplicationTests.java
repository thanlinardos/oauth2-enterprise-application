package com.thanlinardos.discoveryserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringTest
class DiscoveryServerApplicationTests {

	@Test
	void contextLoads() {
		// This test is used to check if the Spring application context loads successfully.
		Assertions.assertDoesNotThrow(() -> {});
	}

}
