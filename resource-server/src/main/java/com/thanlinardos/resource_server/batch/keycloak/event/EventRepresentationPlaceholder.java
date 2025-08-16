package com.thanlinardos.resource_server.batch.keycloak.event;

import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.EventRepresentation;

import java.util.Map;

@Getter
@Setter
public class EventRepresentationPlaceholder {

    private long time;
    private EventType type;
    private String realmId;
    private String clientId;
    private String userId;
    private String sessionId;
    private String ipAddress;
    private String error;
    private Map<String, String> details;

    public EventRepresentationPlaceholder(EventRepresentation event) {
        setType(EventType.fromValue(event.getType()));
        setIpAddress(event.getIpAddress());
        setRealmId(event.getRealmId());
        setUserId(event.getUserId());
        setTime(event.getTime());
        setError(event.getError());
        setClientId(event.getClientId());
        setDetails(event.getDetails());
    }

    @Override
    public String toString() {
        return "EventRepresentationPlaceholder{"
                + "type='" + getType() + '\''
                + ", ipAddress='" + getIpAddress() + '\''
                + ", realmId='" + getRealmId() + '\''
                + ", userId='" + getUserId() + '\''
                + ", time=" + getTime()
                + ", error='" + getError() + '\''
                + ", clientId='" + getClientId() + '\''
                + ", details=" + getDetails()
                + "}";
    }
}
