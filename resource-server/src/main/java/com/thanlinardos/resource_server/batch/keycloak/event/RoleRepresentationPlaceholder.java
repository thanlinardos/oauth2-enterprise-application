package com.thanlinardos.resource_server.batch.keycloak.event;

import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.UUID;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants.SecurityCommonConstants.ROLE_PREFIX;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class RoleRepresentationPlaceholder extends BasicIdModel {

    private UUID uuid;
    private UUID adminEventId;
    private String name;
    private boolean clientRole;

    public static RoleRepresentationPlaceholder fromRepresentation(RoleRepresentation representation) {
        return RoleRepresentationPlaceholder.builder()
                .uuid(UUID.fromString(representation.getId()))
                .name(ROLE_PREFIX + representation.getName())
                .clientRole(representation.getClientRole())
                .build();
    }
}
