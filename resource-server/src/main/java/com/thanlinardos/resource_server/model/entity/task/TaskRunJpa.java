package com.thanlinardos.resource_server.model.entity.task;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task_run")
@AllArgsConstructor
@NoArgsConstructor
public class TaskRunJpa extends BasicIdJpa {

    private String name;
    private LocalDateTime time;
}
