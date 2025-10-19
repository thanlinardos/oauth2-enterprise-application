package com.thanlinardos.discoveryserver;

import com.thanlinardos.spring_enterprise_library.config.TimeConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import(TimeConfig.class)
@TestPropertySource("classpath:application-test.properties")
public @interface SpringTest {
}
