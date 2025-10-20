package com.thanlinardos.discoveryserver;

import com.thanlinardos.spring_enterprise_library.annotations.SpringTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SpringTest
class DiscoveryServerApplicationTests {

	@Test
	void contextLoads() {
		// This test is used to check if the Spring application context loads successfully.
		Assertions.assertDoesNotThrow(() -> {});
	}

}
