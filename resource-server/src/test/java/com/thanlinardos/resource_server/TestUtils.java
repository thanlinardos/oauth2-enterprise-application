package com.thanlinardos.resource_server;

import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.mapped.CustomerModel;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class TestUtils implements WithSecurityContextFactory<WithMockCustomUser> {

    public static final String DEFAULT_USER = "user@email.com";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_OWNER = "ROLE_OWNER";
    public static final GrantedAuthority[] USER_AUTHORITIES = {
            new SimpleGrantedAuthority(ROLE_USER),
            new SimpleGrantedAuthority("READ_USER"),
            new SimpleGrantedAuthority("READ_OWNER"),
            new SimpleGrantedAuthority("READ_CUSTOMERS_USERNAME")
    };
    public static final GrantedAuthority[] MANAGER_AUTHORITIES = {
            new SimpleGrantedAuthority(ROLE_MANAGER),
            new SimpleGrantedAuthority("READ_USER"),
            new SimpleGrantedAuthority("READ_OWNER"),
            new SimpleGrantedAuthority("READ_CUSTOMERS_USERNAME")
    };
    public static final GrantedAuthority[] ADMIN_AUTHORITIES = {
            new SimpleGrantedAuthority(ROLE_ADMIN),
            new SimpleGrantedAuthority("READ_USER"),
            new SimpleGrantedAuthority("READ_OWNER"),
            new SimpleGrantedAuthority("READ_CUSTOMERS_USERNAME"),
            new SimpleGrantedAuthority("ALL_ADMIN"),
            new SimpleGrantedAuthority("CREATE_CUSTOMER"),
    };
    public static final GrantedAuthority[] OWNER_AUTHORITIES = {
            new SimpleGrantedAuthority(ROLE_OWNER),
            new SimpleGrantedAuthority("READ_USER"),
            new SimpleGrantedAuthority("READ_OWNER"),
            new SimpleGrantedAuthority("READ_CUSTOMERS_USERNAME"),
            new SimpleGrantedAuthority("ALL_ADMIN"),
            new SimpleGrantedAuthority("CREATE_CUSTOMER"),
    };

    public static final Jwt ADMIN_PRINCIPAL = Jwt.withTokenValue("admin")
            .header("alg", "none")
            .claim("sub", "admin")
            .claim("email", "admin@email.com")
            .claim("resource_access", List.of("account"))
            .build();

    public static final Jwt USER_PRINCIPAL = Jwt.withTokenValue("user")
            .header("alg", "none")
            .claim("sub", "user")
            .claim("email", "user@email.com")
            .claim("resource_access", List.of("account"))
            .build();

    public static void autoAuthenticateAdminUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(ADMIN_PRINCIPAL, "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_MANAGER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void autoAuthenticateUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(USER_PRINCIPAL, "pass",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static CustomerModel buildTestCustomer(String email) {
        return CustomerModel.builder()
                .username(email.split("@")[0])
                .email(email)
                .firstName("test")
                .lastName("test")
                .privilegeLevel(1)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .createdBy("test")
                .updatedAt(LocalDateTime.now())
                .updatedBy("test")
                .mobileNumber("1234567890")
                .build();
    }

    public static OwnerModel buildTestOwner(String email, List<RoleModel> roles) {
        return OwnerModel.builder()
                .type(OwnerType.CUSTOMER)
                .principalName(email)
                .customer(TestUtils.buildTestCustomer(email))
                .privilegeLevel(3)
                .roles(roles)
                .build();
    }

    public static ReflectiveMethodInvocation createDummyReflectiveMethodInvocation(Method method) {
        try {
            Constructor<ReflectiveMethodInvocation> constructor = ReflectiveMethodInvocation.class
                    .getDeclaredConstructor(Object.class, Object.class, Method.class, Object[].class, Class.class, List.class);
            constructor.setAccessible(true);
            return constructor.newInstance(null, null, method, null, null, null);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodInvocationProceedingJoinPoint createDummyJoinPoint(Method method) {
        return new MethodInvocationProceedingJoinPoint(Objects.requireNonNull(TestUtils.createDummyReflectiveMethodInvocation(method)));
    }

    public static JoinPoint createDummyJoinPointForMethod(Method method) {
        MethodInvocationProceedingJoinPoint joinPoint = createDummyJoinPoint(method);
        setJoinPointSignature(joinPoint, getMethodSignature(method));
        return joinPoint;
    }

    public static void setJoinPointSignature(MethodInvocationProceedingJoinPoint joinPoint, MethodSignature methodSignature) {
        Field signatureField = Arrays.stream(MethodInvocationProceedingJoinPoint.class.getDeclaredFields())
                .filter(field -> field.getName().equals("signature"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No field named 'signature' found in MethodInvocationProceedingJoinPoint class"));
        signatureField.setAccessible(true);
        try {
            signatureField.set(joinPoint, methodSignature);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodSignature getMethodSignature(Method method) {
        try {
            Class<?> clazz = Class.forName("org.aspectj.runtime.reflect.MethodSignatureImpl");
            Constructor<?> constructor = clazz.getDeclaredConstructor(int.class, String.class, Class.class, Class[].class, String[].class, Class[].class, Class.class);
            constructor.setAccessible(true);
            return (MethodSignature) constructor.newInstance(method.getModifiers(), method.getName(), method.getDeclaringClass(), method
                    .getParameterTypes(), new String[method.getParameterTypes().length], method.getExceptionTypes(), method
                    .getReturnType());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Jwt principal = Jwt.withTokenValue(customUser.name())
                .header("alg", "none")
                .claim("sub", customUser.name())
                .claim("email", customUser.name())
                .claim("resource_access", Map.of("account", "account"))
                .build();
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal,
                customUser.password(),
                Arrays.stream(customUser.roles())
                        .map(TestUtils::buildRole)
                        .flatMap(TestUtils::getGrantedAuthorityStreamIncludingRoleName)
                        .distinct()
                        .toList()
        );
        context.setAuthentication(auth);
        return context;
    }

    private static @NotNull Stream<GrantedAuthority> getGrantedAuthorityStreamIncludingRoleName(RoleModel role) {
        return switch (role.getName()) {
            case ROLE_USER -> Arrays.stream(USER_AUTHORITIES);
            case ROLE_MANAGER -> Arrays.stream(MANAGER_AUTHORITIES);
            case ROLE_ADMIN -> Arrays.stream(ADMIN_AUTHORITIES);
            case ROLE_OWNER -> Arrays.stream(OWNER_AUTHORITIES);
            default -> Stream.empty();
        };
    }

    public static RoleModel buildRole(String name) {
        return RoleModel.builder()
                .role(name)
                .build();
    }
}
