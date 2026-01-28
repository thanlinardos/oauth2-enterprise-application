package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.info.TaskType;

public interface CustomTaskRunRepository {

    long getTaskRunTime(TaskType taskName);

    long updateTaskRunTime(TaskType taskName, long time);
}
