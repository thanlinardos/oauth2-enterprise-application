package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.task.TaskRunJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRunRepository extends JpaRepository<TaskRunJpa, Long>, CustomTaskRunRepository {
}
