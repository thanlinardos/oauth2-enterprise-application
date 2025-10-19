package com.thanlinardos.resource_server.batch.keycloak.event;

import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.AuthDetailsRepresentation;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class AdminEventRepresentationPlaceholder {

    private long time;
    private String realmId;
    private AuthDetailsRepresentation authDetails;
    private AdminEventOperationType operationType;
    private AdminEventResourceType resourceType;
    private String resourcePath;
    private String representation;
    private String error;
    @Nullable private UUID resourceId;
    @Nullable private ResourceIdType resourceIdType;

    public AdminEventRepresentationPlaceholder(AdminEventRepresentation event) {
        setTime(event.getTime());
        setRealmId(event.getRealmId());
        setAuthDetails(event.getAuthDetails());
        setOperationType(AdminEventOperationType.fromValue(event.getOperationType()));
        setResourceType(AdminEventResourceType.fromValue(event.getResourceType()));
        setResourcePath(event.getResourcePath());
        setRepresentation(event.getRepresentation());
        setError(event.getError());
        setResourceId(parseResourceIdFromPath());
        setResourceIdType(parseResourceIdTypeFromPath());
    }

    private ResourceIdType parseResourceIdTypeFromPath() {
        return Optional.ofNullable(resourcePath)
                .map(path -> path.split("/")[0])
                .map(ResourceIdType::fromValue)
                .orElse(null);
    }

    @Nullable
    private UUID parseResourceIdFromPath() {
        return Optional.ofNullable(resourcePath)
                .map(path -> path.split("/"))
                .filter(parts -> parts.length > 1)
                .map(parts -> parts[1])
                .map(ParserUtil::safeParseUUID)
                .orElse(null);
    }

    @Override
    public String toString() {
        return "AdminEventRepresentationPlaceholder{"
                + "time=" + getTime()
                + ", realmId='" + getRealmId() + '\''
                + ", authDetails=" + getAuthDetails()
                + ", operationType='" + getOperationType() + '\''
                + ", resourceType='" + getResourceType() + '\''
                + ", resourcePath='" + getResourcePath() + '\''
                + ", resourceId='" + getResourceId() + '\''
                + ", resourceIdType='" + getResourceIdType() + '\''
                + ", representation=" + getRepresentation()
                + ", error=" + getError()
                + '}';
    }
}
