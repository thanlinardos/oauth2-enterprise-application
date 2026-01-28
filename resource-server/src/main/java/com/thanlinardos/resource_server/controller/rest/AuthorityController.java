package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.info.AuthorityInfo;
import com.thanlinardos.resource_server.model.mapped.AuthorityModel;
import com.thanlinardos.resource_server.service.role.RoleCacheService;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorityController {

    private final RoleCacheService roleCacheService;

    @PostMapping("/authorities")
    public ResponseEntity<Authority> createAuthority(@RequestBody AuthorityInfo authorityInfo) {
        AuthorityModel model = AuthorityModel.builder()
                .name(authorityInfo.name())
                .accessType(authorityInfo.access())
                .uri(authorityInfo.uri())
                .expression(authorityInfo.expression())
                .build();
        model = (AuthorityModel) roleCacheService.addAuthority(model);
        for (String role : authorityInfo.roles()) {
            roleCacheService.linkAuthorityToRole(model.getId(), role);
        }
        return ResponseEntity.ok(model);
    }
}
