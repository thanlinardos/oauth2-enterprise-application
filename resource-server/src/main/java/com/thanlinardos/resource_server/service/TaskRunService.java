package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.misc.utils.DateUtils;
import com.thanlinardos.resource_server.model.info.TaskType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskRunService {

    private final EntityManager entityManager;

    public long getTaskRunTime(TaskType taskName) {
        return DateUtils.getEpochMilliFromLocalDateTime(entityManager.createQuery("SELECT tr.time FROM TaskRunJpa tr WHERE tr.name = :taskName", LocalDateTime.class)
                .setParameter("taskName", taskName.name())
                .getSingleResult());
    }

    @Transactional
    public void updateTaskRunTime(TaskType taskName, long time) {
        if (time != getTaskRunTime(taskName)) {
            entityManager.createQuery("UPDATE TaskRunJpa tr SET tr.time = :time WHERE tr.name = :taskName")
                    .setParameter("time", DateUtils.getLocalDateTimeFromEpochMilli(time))
                    .setParameter("taskName", taskName.name())
                    .executeUpdate();
        }
    }
}
