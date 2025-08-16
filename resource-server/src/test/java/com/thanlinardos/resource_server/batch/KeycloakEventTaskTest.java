package com.thanlinardos.resource_server.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanlinardos.resource_server.batch.keycloak.event.AdminEventRepresentationPlaceholder;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.service.TaskRunService;
import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class KeycloakEventTaskTest {

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaskRunService taskRunService;
    @Mock
    private OauthRoleService roleService;

    @InjectMocks
    private KeycloakEventTask keycloakEventTask;

    @Test
    void parseRolesFromEvent() throws JsonProcessingException {
        AdminEventRepresentation event = new AdminEventRepresentation();
        event.setOperationType("CREATE");
        event.setResourceType("REALM_ROLE_MAPPING");
        event.setTime(System.currentTimeMillis());
        event.setRealmId("realm");
        event.setResourcePath("users/" + UUID.randomUUID());

        RoleRepresentation role1 = new RoleRepresentation();
        role1.setName("GUEST");
        role1.setId(UUID.randomUUID().toString());

        RoleRepresentation role2 = new RoleRepresentation();
        role2.setName("USER");
        role2.setId(UUID.randomUUID().toString());

        event.setRepresentation(objectMapper.writeValueAsString(List.of(role1, role2)));
        AdminEventRepresentationPlaceholder eventRepresentation = new AdminEventRepresentationPlaceholder(event);

        Collection<RoleModel> actual = keycloakEventTask.parseRolesFromEvent(eventRepresentation);
        Assertions.assertNotNull(actual);
    }
}