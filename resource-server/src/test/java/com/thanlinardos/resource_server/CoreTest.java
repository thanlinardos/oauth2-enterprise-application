package com.thanlinardos.resource_server;

import com.thanlinardos.spring_enterprise_library.config.TimeConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(MockitoExtension.class)
@Import(TimeConfig.class)
public @interface CoreTest {
}
