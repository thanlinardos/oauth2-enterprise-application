package com.thanlinardos.resource_server.model.mapped.base;

import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@SuperBuilder
public class BasicIdModel implements Serializable {

    private Long id;

    public BasicIdModel() {
    }

    protected BasicIdModel(BasicIdJpa entity) {
        setId(entity.getId());
    }
}
