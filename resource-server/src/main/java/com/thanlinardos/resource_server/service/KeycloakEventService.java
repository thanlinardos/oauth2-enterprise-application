package com.thanlinardos.resource_server.service;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RefreshScope
@Service
@ConditionalOnExpression("'${scheduling.enabled}'=='true' && '${oauth2.auth-server}' == 'KEYCLOAK'")
public class KeycloakEventService {

    private final RealmResource realm;

    public KeycloakEventService(Keycloak keycloak, @Value("${oauth2.keycloak.realm}") String realmName) {
        this.realm = keycloak.realm(realmName);
    }

    public List<EventRepresentation> fetchEvents() {
        return realm.getEvents();
    }

    public List<AdminEventRepresentation> fetchAdminEvents() {
        return realm.getAdminEvents();
    }
}
