package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
