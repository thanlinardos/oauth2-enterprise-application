package com.thanlinardos.resource_server.batch.keycloak.event;

import com.thanlinardos.resource_server.model.info.TaskType;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class EventPlaceholder extends BasicIdModel {

    private UUID uuid;
    private long time;
    private EventStatusType status;
    private UUID realmId;
    private String error;

    public EventPlaceholder(UUID uuid, long id, long time, EventStatusType status, UUID realmId, String error) {
        super(id);
        this.uuid = uuid;
        this.time = time;
        this.status = status;
        this.realmId = realmId;
        this.error = error;
    }

    public boolean isFailed() {
        return status.isFailed();
    }

    public boolean isNotSkippedAsFailed() {
        return !isSkippedAsFailed();
    }

    public boolean isNotIgnored() {
        return !isIgnored();
    }

    private boolean isIgnored() {
        return EventStatusType.IGNORED.equals(status);
    }

    private boolean isSkippedAsFailed() {
        return EventStatusType.SKIPPED_AS_FAILED.equals(status);
    }

    public abstract UUID getResourceId();

    public TaskType getTaskType() {
        return TaskType.KEYCLOAK_EVENT_TASK;
    }
}
