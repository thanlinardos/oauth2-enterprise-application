package com.thanlinardos.resource_server.security;

import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.SecurityCommonConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
public class SecurityProdConfig extends SecurityCommonConfig<RoleModel> {

    private final OauthRoleService roleService;

    public SecurityProdConfig(OauthRoleService roleService) {
        super(roleService);
        this.roleService = roleService;
    }

    @Override
    protected Collection<Authority> getAuthorities() {
        return roleService.getAllAuthorities();
    }

    @Bean
    @Order(1)
    @Override
    protected SecurityFilterChain userLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> buildProdCorsConfiguration()));
        return super.userLoginSecurityFilterChain(http);
    }

    private CorsConfiguration buildProdCorsConfiguration() {
        return buildCorsConfiguration(
                List.of("GET", "HEAD", "POST", "PUT", "OPTIONS", "PATCH"),
                Collections.singletonList("Authorization"),
                600L
        );
    }
}
