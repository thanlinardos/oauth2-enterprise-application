package com.thanlinardos.resource_server.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanlinardos.resource_server.batch.keycloak.event.*;
import com.thanlinardos.resource_server.misc.utils.RoleUtils;
import com.thanlinardos.resource_server.model.info.OwnerType;
import com.thanlinardos.resource_server.model.info.TaskType;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.resource_server.service.ClientService;
import com.thanlinardos.resource_server.service.KeycloakEventService;
import com.thanlinardos.resource_server.service.OwnerService;
import com.thanlinardos.resource_server.service.TaskRunService;
import com.thanlinardos.resource_server.service.roleservice.api.OauthRoleService;
import com.thanlinardos.resource_server.service.userservice.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Component
@Slf4j
@ConditionalOnExpression("'${scheduling.enabled}'=='true' && '${oauth2.auth-server}' == 'KEYCLOAK'")
public class KeycloakEventTask {

    private static final String EMAIL = "email";
    private static final String FAILED_TO_PARSE_ID_FROM_RESOURCE_PATH = "Failed to parse id from resource path:";

    private final ObjectMapper objectMapper;
    private final OwnerService ownerService;
    private final ClientService clientService;
    private final UserService userService;
    private final KeycloakEventService keycloakEventService;
    private final TaskRunService taskRunService;
    private final OauthRoleService roleService;

    private long lastEventTime;
    private long lastAdminEventTime;

    public KeycloakEventTask(ObjectMapper objectMapper,
                             OwnerService ownerService,
                             ClientService clientService,
                             UserService userService,
                             KeycloakEventService keycloakEventService,
                             TaskRunService taskRunService,
                             OauthRoleService roleService) {
        this.objectMapper = objectMapper;
        this.ownerService = ownerService;
        this.clientService = clientService;
        this.userService = userService;
        this.keycloakEventService = keycloakEventService;
        this.taskRunService = taskRunService;
        this.lastEventTime = taskRunService.getTaskRunTime(TaskType.KEYCLOAK_EVENT_TASK);
        this.lastAdminEventTime = taskRunService.getTaskRunTime(TaskType.KEYCLOAK_ADMIN_EVENT_TASK);
        this.roleService = roleService;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchKeycloakEvents() {
        keycloakEventService.fetchEvents().stream()
                .sorted(Comparator.comparingLong(EventRepresentation::getTime))
                .filter(e -> e.getTime() > lastEventTime)
                .map(EventRepresentationPlaceholder::new)
                .map(this::tryHandleEvent)
                .max(Comparator.comparingLong(EventRepresentationPlaceholder::getTime))
                .ifPresent(this::updateTaskRunTime);
    }

    private void updateTaskRunTime(EventRepresentationPlaceholder e) {
        lastEventTime = e.getTime();
        taskRunService.updateTaskRunTime(TaskType.KEYCLOAK_EVENT_TASK, lastEventTime);
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchKeycloakAdminEvents() {
        keycloakEventService.fetchAdminEvents().stream()
                .sorted(Comparator.comparingLong(AdminEventRepresentation::getTime))
                .filter(e -> e.getTime() > lastAdminEventTime)
                .map(AdminEventRepresentationPlaceholder::new)
                .map(this::tryHandleAdminEvent)
                .max(Comparator.comparingLong(AdminEventRepresentationPlaceholder::getTime))
                .ifPresent(this::updateAdminTaskRunTime);
    }

    private void updateAdminTaskRunTime(AdminEventRepresentationPlaceholder e) {
        lastAdminEventTime = e.getTime();
        taskRunService.updateTaskRunTime(TaskType.KEYCLOAK_ADMIN_EVENT_TASK, lastAdminEventTime);
    }

    private EventRepresentationPlaceholder tryHandleEvent(EventRepresentationPlaceholder event) {
        log.trace("Keycloak event: {}", event);
        try {
            handleEvent(event);
        } catch (Exception e) {
            logEvent(Level.ERROR, event, "Failed to handle event with error", e);
        }
        return event;
    }

    private void handleEvent(EventRepresentationPlaceholder event) {
        switch (event.getType()) {
            case REGISTER -> getGuestOwnerByEmailAndPersist(event);
            case UPDATE_TOTP, SEND_VERIFY_EMAIL ->
                    userService.getOwnerByIdAndPersistOrUpdate(UUID.fromString(event.getUserId()), OwnerType.CUSTOMER)
                            .ifPresentOrElse(
                                    unused -> {
                                    },
                                    () -> logEvent(Level.ERROR, event, "Failed to update user", event.getUserId())
                            );
            default -> log.trace("Unhandled event: {}", event);
        }
    }

    private void getGuestOwnerByEmailAndPersist(EventRepresentationPlaceholder event) {
        if (ownerService.ownerExistsByPrincipalName(event.getDetails().get(EMAIL))) {
            logEvent(Level.ERROR, event, "Failed to register guest user. A user already exists with email", event.getDetails().get(EMAIL));
        } else {
        userService.getGuestOwnerByEmailAndPersist(event.getDetails().get(EMAIL))
                .ifPresentOrElse(
                        e -> logEvent(Level.INFO, event, "Registered guest user with email", e),
                        () -> logEvent(Level.ERROR, event, "Failed to register guest user", event.getDetails().get(EMAIL))
                );
        }
    }

    private AdminEventRepresentationPlaceholder tryHandleAdminEvent(AdminEventRepresentationPlaceholder event) {
        log.trace("Keycloak admin event: {}", event);
        if (event.getResourceType() == null) {
            logAdminEvent(Level.WARN, event, "Resource type is null", event);
            return event;
        }
        try {
            switch (event.getResourceType()) {
                case REALM_ROLE_MAPPING -> handleRealmRoleMapping(event);
                case USER -> handleUserEvent(event);
                case CLIENT -> handleClientEvent(event);
                default -> logAdminEvent(Level.WARN, event, "Unhandled resource type", event.getResourceType());
            }
        } catch (Exception e) {
            logAdminEvent(Level.ERROR, event, "Failed to handle admin event with error:", e);
        }
        return event;
    }

    private void handleUserEvent(AdminEventRepresentationPlaceholder event) {
        if (isNotValidResourceId(event)) {
            logAdminEvent(Level.WARN, event, FAILED_TO_PARSE_ID_FROM_RESOURCE_PATH, event.getResourcePath());
            return;
        }
        UUID userId = event.getResourceId();
        switch (event.getOperationType()) {
            case DELETE -> ownerService.delete(userId)
                    .ifPresent(unused -> logAdminEvent(Level.INFO, event, "Deleted user", userId));
            case CREATE -> handleOwnerCreation(event, userId, OwnerType.CUSTOMER);
            case UPDATE -> handleOwnerUpdate(event, userId, OwnerType.CUSTOMER);
            case ACTION -> {
                if (event.getResourcePath().contains("reset-password")) {
                    logAdminEvent(Level.INFO, event, "Reset password for user", userId);
                } else {
                    logAdminEvent(Level.WARN, event, "Unhandled action", event.getResourcePath());
                }
            }
        }
    }

    private void handleOwnerUpdate(AdminEventRepresentationPlaceholder event, UUID ownerUuid, OwnerType ownerType) {
        userService.getOwnerByIdAndPersistOrUpdate(ownerUuid, ownerType)
                .ifPresentOrElse(
                        owner -> logAdminEvent(Level.INFO, event, "Updated " + ownerType.name().toLowerCase(), owner),
                        () -> logAdminEvent(Level.ERROR, event, "Failed to update " + ownerType.name().toLowerCase(), ownerUuid)
                );
    }

    private void handleOwnerCreation(AdminEventRepresentationPlaceholder event, UUID ownerUuid, OwnerType ownerType) {
        ownerService.getOwnerByUuid(ownerUuid).ifPresentOrElse(unused -> {
                },
                () -> getOwnerByIdAndPersist(ownerUuid)
                        .ifPresentOrElse(
                                owner -> logAdminEvent(Level.INFO, event, "Created " + ownerType.name().toLowerCase(), owner),
                                () -> logAdminEvent(Level.ERROR, event, "Failed to create " + ownerType.name().toLowerCase(), ownerUuid)
                        ));
    }

    private Optional<OwnerModel> getOwnerByIdAndPersist(UUID userId) {
        return userService.getOwnerByIdAndPersistOrUpdate(userId, OwnerType.CUSTOMER);
    }

    private void handleClientEvent(AdminEventRepresentationPlaceholder event) {
        if (isNotValidResourceId(event)) {
            logAdminEvent(Level.WARN, event, FAILED_TO_PARSE_ID_FROM_RESOURCE_PATH, event.getResourcePath());
            return;
        }
        UUID clientId = event.getResourceId();
        switch (event.getOperationType()) {
            case DELETE -> ownerService.delete(clientId)
                    .ifPresent(unused -> logAdminEvent(Level.INFO, event, "Deleted client", clientId));
            case CREATE -> handleOwnerCreation(event, clientId, OwnerType.CLIENT);
            case UPDATE -> handleOwnerUpdate(event, clientId, OwnerType.CLIENT);
            default -> logAdminEvent(Level.WARN, event, "Unhandled operation type", event.getOperationType());
        }
    }

    private boolean isNotValidResourceId(AdminEventRepresentationPlaceholder event) {
        return event.getResourceIdType() == null || event.getResourceId() == null;
    }

    private void handleRealmRoleMapping(AdminEventRepresentationPlaceholder event) {
        Collection<RoleModel> roles = parseRolesFromEvent(event);
        if (roles.isEmpty()) {
            return;
        }
        logAdminEvent(Level.INFO, event, "roles", roles);

        if (isNotValidResourceId(event)) {
            logAdminEvent(Level.WARN, event, FAILED_TO_PARSE_ID_FROM_RESOURCE_PATH, event.getResourcePath());
            return;
        }
        if (event.getResourceIdType() == ResourceIdType.USERS) {
            UUID userId = event.getResourceId();
            Optional<OwnerModel> foundOwner = ownerService.getOwnerByUuid(userId)
                    .or(() -> clientService.getOwnerByServiceAccountId(userId));
            switch (event.getOperationType()) {
                case CREATE -> foundOwner.ifPresent(owner -> addRolesAndUpdateOwner(event, owner, roles));
                case DELETE -> foundOwner.ifPresent(owner -> deleteRolesAndUpdateOwner(owner, roles));
                default -> logAdminEvent(Level.WARN, event, "Unhandled operation type", event.getOperationType());
            }
        }
    }

    private void addRolesAndUpdateOwner(AdminEventRepresentationPlaceholder event, OwnerModel owner, Collection<RoleModel> roles) {
        if (new HashSet<>(owner.getRoles()).containsAll(roles)) {
            logAdminEvent(Level.WARN, event, "Owner already has roles", roles);
        } else {
            owner.setRoles(Stream.concat(owner.getRoles().stream(), roles.stream()
                            .filter(r -> !owner.getRoles().contains(r)))
                    .toList());
            ownerService.save(owner);
        }
    }

    private void deleteRolesAndUpdateOwner(OwnerModel owner, Collection<RoleModel> roles) {
        owner.setRoles(owner.getRoles().stream()
                .filter(r -> !roles.contains(r))
                .toList());
        ownerService.save(owner);
    }

    private void logEvent(Level level, EventRepresentationPlaceholder event, String message, Object parsedResource) {
        log.atLevel(level).log("[KEYCLOAK_EVENT][{}] {}: {}", event.getType(), message, parsedResource);
    }

    private void logAdminEvent(Level level, AdminEventRepresentationPlaceholder event, String message, Object parsedResource) {
        log.atLevel(level).log("[KEYCLOAK_ADMIN_EVENT][{} {}] {}: {}", event.getOperationType(), event.getResourceType(), message, parsedResource);
    }

    Collection<RoleModel> parseRolesFromEvent(AdminEventRepresentationPlaceholder event) {
        try {
            List<String> roleNames = parseRoleNamesFromEvent(event);
            return roleService.findRoles(roleNames);
        } catch (IOException | ClassCastException e) {
            logAdminEvent(Level.ERROR, event, "Failed to parse roles from event", e.getMessage());
            return Collections.emptyList();
        }
    }

    private @NotNull List<String> parseRoleNamesFromEvent(AdminEventRepresentationPlaceholder event) throws JsonProcessingException {
        List<RoleRepresentation> roleRepresentations = objectMapper.readerForListOf(RoleRepresentation.class).readValue(event.getRepresentation());
        return RoleUtils.getRoleNamesFromRoleRepresentations(roleRepresentations);
    }
}
