package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class Client extends OwnerDetailsInfo implements Serializable {

    private String category;
    private UUID serviceAccountId;
}
