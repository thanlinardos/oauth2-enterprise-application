package com.thanlinardos.resource_server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ResourceServerApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }
}
