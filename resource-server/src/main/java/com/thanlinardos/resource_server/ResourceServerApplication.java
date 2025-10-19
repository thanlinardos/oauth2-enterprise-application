package com.thanlinardos.resource_server;

import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.service.UserRoleCacheService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Collection;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
//@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
public class ResourceServerApplication {

	private final UserRoleCacheService userRoleCacheService;

    public static void main(String[] args) {
		SpringApplication.run(ResourceServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			Collection<RoleModel> roles = userRoleCacheService.getAllRoles();
			List<Authority> authorities = userRoleCacheService.getAllAuthorities();
			log.info("Loaded {} roles and {} authorities", roles.size(), authorities.size());
		};
	}
}
