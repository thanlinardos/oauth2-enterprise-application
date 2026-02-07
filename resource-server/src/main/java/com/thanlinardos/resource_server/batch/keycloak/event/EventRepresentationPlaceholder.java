package com.thanlinardos.resource_server.batch.keycloak.event;

import com.thanlinardos.resource_server.model.entity.keycloak.KeycloakEventDetailsJpa;
import com.thanlinardos.resource_server.model.entity.keycloak.KeycloakEventJpa;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.EventRepresentation;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class EventRepresentationPlaceholder extends EventPlaceholder {

    private KeycloakUserEventType type;
    @Nullable private UUID clientId;
    @Nullable private UUID userId;
    private Map<String, String> details;

    public EventRepresentationPlaceholder(EventRepresentation event) {
        super(UUID.fromString(event.getId()), event.getTime(), EventStatusType.RECEIVED, UUID.fromString(event.getRealmId()), event.getError());
        setType(KeycloakUserEventType.fromValue(event.getType()));
        if (event.getClientUuid() == null) {
            setUserId(UUID.fromString(event.getUserId()));
        } else {
            setClientId(UUID.fromString(event.getClientUuid()));
        }
        setDetails(event.getDetails());
    }

    public EventRepresentationPlaceholder(KeycloakEventJpa entity) {
        super(entity.getUuid(), entity.getId(), DateUtils.getEpochMilliFromLocalDateTime(entity.getTime()), entity.getStatus(), entity.getRealmId(), entity.getError());
        this.type = entity.getType();
        this.clientId = entity.getClientId();
        this.userId = entity.getUserId();
        this.details = KeycloakEventDetailsJpa.toMap(entity.getDetails());
    }

    @Override
    public UUID getResourceId() {
        return Optional.ofNullable(getUserId())
                .orElse(getClientId());
    }

    @Override
    public String toString() {
        return "EventRepresentationPlaceholder{"
                + "uuid='" + getUuid() + '\''
                + ", type='" + getType() + '\''
                + ", status=" + getStatus()
                + ", realmId='" + getRealmId() + '\''
                + ", userId='" + getUserId() + '\''
                + ", time=" + getTime()
                + ", error='" + getError() + '\''
                + ", clientId='" + getClientId() + '\''
                + ", details=" + getDetails()
                + "}";
    }
}
