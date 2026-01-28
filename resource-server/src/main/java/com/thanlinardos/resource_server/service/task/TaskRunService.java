package com.thanlinardos.resource_server.service.task;

import com.thanlinardos.resource_server.aspect.annotation.ExcludeFromLoggingAspect;
import com.thanlinardos.resource_server.model.info.TaskType;
import com.thanlinardos.resource_server.repository.api.TaskRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskRunService {

    private final TaskRunRepository taskRunRepository;

    @ExcludeFromLoggingAspect
    @Cacheable(value = "taskRunTimes", key = "#taskName")
    public long getTaskRunTime(TaskType taskName) {
        return taskRunRepository.getTaskRunTime(taskName);
    }

    @ExcludeFromLoggingAspect
    @Transactional
    @CachePut(value = "taskRunTimes", key = "#taskName")
    public long updateTaskRunTime(TaskType taskName, long time) {
        return taskRunRepository.updateTaskRunTime(taskName, time);
    }
}
