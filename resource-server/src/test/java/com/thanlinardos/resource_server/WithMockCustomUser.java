package com.thanlinardos.resource_server;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.thanlinardos.resource_server.TestUtils.DEFAULT_USER;
import static com.thanlinardos.resource_server.TestUtils.ROLE_USER;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestUtils.class)
public @interface WithMockCustomUser {

    String name() default DEFAULT_USER;

    String password() default "pass";

    String[] roles() default {ROLE_USER};
}
