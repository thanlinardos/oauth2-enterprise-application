package com.thanlinardos.resource_server.misc.utils;

import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.function.Consumer;

public class EntityUtils {

    private EntityUtils() {
    }

    @Nullable
    public static <T extends BasicIdJpa> T buildEntityWithIdOrNull(@Nullable Long entityId) {
        return (T) Optional.ofNullable(entityId)
                .map(id -> buildEntityWithId(entityId))
                .orElse(null);
    }

    public static <T extends BasicIdJpa> T buildEntityWithId(Long entityId) {
        return (T) BasicIdJpa.builder()
                .id(entityId)
                .build();
    }

    public static <T extends BasicIdJpa, R extends BasicIdJpa> void addMemberWithLink(T entity, R member, Consumer<T> memberSetter, Collection<R> memberList) {
        memberSetter.accept(entity);
        memberList.add(member);
    }

    public static <T extends BasicIdJpa> T saveOrUpdate(T entity, EntityManager entityManager) {
        if (entity.getId() == null) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }
    }
}
