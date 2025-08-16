package com.thanlinardos.resource_server.model.mapped.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanlinardos.resource_server.model.entity.base.BasicIdJpa;
import com.thanlinardos.resource_server.model.entity.base.BasicOwnedEntity;
import com.thanlinardos.resource_server.model.OwnedResource;
import com.thanlinardos.resource_server.model.mapped.OwnerModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BasicOwnedIdModel extends BasicIdModel implements OwnedResource<BasicOwnedIdModel> {

    @ToString.Exclude
    @JsonIgnore
    private OwnerModel owner;

    public BasicOwnedIdModel() {
        super();
    }

    protected BasicOwnedIdModel(BasicOwnedEntity entity) {
        super((BasicIdJpa) entity);
        this.setOwner(new OwnerModel(entity.getOwner()));
    }
}
