package com.thanlinardos.authorizationserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class AuthorizationServerApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }
}
