package com.thanlinardos.resource_server.aspect;

import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.aspect.AuthorizationAspectHelper;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("service_authorization")
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true")
public class EventTaskServiceAuthAspect {

    private final OauthRoleService roleService;

    @Before("com.thanlinardos.resource_server.aspect.PointCutDefinitions.forBatchEventTasks()")
    public void refreshOwnerSecurityContext(JoinPoint jp) {
        AuthorizationAspectHelper.refreshOwnerSecurityContext(jp, getAllGrantedAuthorities());
    }

    private @NotNull List<GrantedAuthority> getAllGrantedAuthorities() {
        return roleService.getAllAuthorities().stream()
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
