package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class KeycloakUserAccess implements Serializable {

    @Builder.Default
    private boolean manageGroupMembership = false;
    @Builder.Default
    private boolean view = false;
    @Builder.Default
    private boolean mapRoles = false;
    @Builder.Default
    private boolean impersonate = false;
    @Builder.Default
    private boolean manage = false;
}
