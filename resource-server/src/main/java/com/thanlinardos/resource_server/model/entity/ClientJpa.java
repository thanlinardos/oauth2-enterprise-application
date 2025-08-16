package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.misc.convert.UUIDConverter;
import com.thanlinardos.resource_server.model.entity.base.BasicAuditableJpa;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "client")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class ClientJpa extends BasicAuditableJpa {

    @OneToOne(mappedBy = "client")
    @ToString.Exclude
    private OwnerJpa owner;
    private String name;
    private String category;
    @Convert(converter = UUIDConverter.class)
    private UUID serviceAccountId;

    public static ClientJpa fromModel(ClientModel client) {
        return builder()
                .id(client.getId())
                .name(client.getName())
                .category(client.getCategory())
                .owner(OwnerJpa.builder()
                        .id(client.getOwnerId())
                        .build())
                .serviceAccountId(client.getServiceAccountId())
                .build();
    }
}
