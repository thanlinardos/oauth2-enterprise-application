package com.thanlinardos.resource_server.batch.keycloak.event;

import com.thanlinardos.resource_server.model.entity.keycloak.KeycloakAdminEventJpa;
import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.AdminEventRepresentation;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AdminEventRepresentationPlaceholder extends EventPlaceholder {

    private AdminEventOperationType operationType;
    private AdminEventResourceType resourceType;
    private String resourcePath;
    @Nullable private String representation;
    private Set<RoleRepresentationPlaceholder> roles = new HashSet<>();
    @Nullable private UUID resourceId;
    @Nullable private ResourceIdType resourceIdType;

    public AdminEventRepresentationPlaceholder(AdminEventRepresentation event) {
        super(UUID.fromString(event.getId()), event.getTime(), EventStatusType.RECEIVED, UUID.fromString(event.getRealmId()), event.getError());
        setOperationType(AdminEventOperationType.fromValue(event.getOperationType()));
        setResourceType(AdminEventResourceType.fromValue(event.getResourceType()));
        setResourcePath(event.getResourcePath());
        setRepresentation(event.getRepresentation());
        setResourceId(parseResourceIdFromPath(resourcePath));
        setResourceIdType(parseResourceIdTypeFromPath(resourcePath));
    }

    public AdminEventRepresentationPlaceholder(KeycloakAdminEventJpa entity) {
        super(entity.getUuid(), entity.getId(), DateUtils.getEpochMilliFromLocalDateTime(entity.getTime()), entity.getStatus(), entity.getRealmId(), entity.getError());
        setOperationType(entity.getOperationType());
        setResourceType(entity.getResourceType());
        setResourcePath(entity.getResourcePath());
        setResourceId(parseResourceIdFromPath(entity.getResourcePath()));
        setResourceIdType(parseResourceIdTypeFromPath(entity.getResourcePath()));
    }

    private ResourceIdType parseResourceIdTypeFromPath(String resourcePath) {
        return Optional.ofNullable(resourcePath)
                .map(path -> path.split("/")[0])
                .map(ResourceIdType::fromValue)
                .orElse(null);
    }

    @Nullable
    private UUID parseResourceIdFromPath(String resourcePath) {
        return Optional.ofNullable(resourcePath)
                .map(path -> path.split("/"))
                .filter(parts -> parts.length > 1)
                .map(parts -> parts[1])
                .map(ParserUtil::safeParseUUID)
                .orElse(null);
    }

    @Nullable
    public UUID getClientId() {
        return Optional.ofNullable(resourceIdType)
                .map(type -> type.equals(ResourceIdType.CLIENTS) ? resourceId : null)
                .orElse(null);
    }

    @Nullable
    public UUID getUserId() {
        return Optional.ofNullable(resourceIdType)
                .map(type -> type.equals(ResourceIdType.USERS) ? resourceId : null)
                .orElse(null);
    }

    @Override
    public String toString() {
        return "AdminEventRepresentationPlaceholder{"
                + "uuid=" + getUuid()
                + ", time=" + getTime()
                + ", status=" + getStatus()
                + ", realmId='" + getRealmId() + '\''
                + ", operationType='" + getOperationType() + '\''
                + ", resourceType='" + getResourceType() + '\''
                + ", resourcePath='" + getResourcePath() + '\''
                + ", resourceId='" + getResourceId() + '\''
                + ", resourceIdType='" + getResourceIdType() + '\''
                + ", representation=" + getRepresentation()
                + ", roles=" + getRoles()
                + ", error=" + getError()
                + '}';
    }
}
