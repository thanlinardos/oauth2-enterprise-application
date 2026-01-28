package com.thanlinardos.resource_server.repository;

import com.thanlinardos.resource_server.model.info.TaskType;
import com.thanlinardos.resource_server.repository.api.CustomTaskRunRepository;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/** IMPORTANT: In order for jpa to pickup custom implementations, they need to follow this exact format as shown in this class: <interface name extending Jpa- or CrudRepository>Impl */
@RequiredArgsConstructor
@Repository
public class TaskRunRepositoryImpl implements CustomTaskRunRepository {

    private final EntityManager entityManager;

    @Override
    public long getTaskRunTime(TaskType taskName) {
        return DateUtils.getEpochMilliFromLocalDateTime(entityManager.createQuery("SELECT tr.time FROM TaskRunJpa tr WHERE tr.name = :taskName", LocalDateTime.class)
                .setParameter("taskName", taskName.name())
                .getSingleResult());
    }

    @Override
    public long updateTaskRunTime(TaskType taskName, long time) {
        entityManager.createQuery("UPDATE TaskRunJpa tr SET tr.time = :time WHERE tr.name = :taskName")
                .setParameter("time", DateUtils.getLocalDateTimeFromEpochMilli(time))
                .setParameter("taskName", taskName.name())
                .executeUpdate();
        return time;
    }
}
