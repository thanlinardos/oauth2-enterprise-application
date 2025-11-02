package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicAuditableJpa;
import com.thanlinardos.resource_server.model.mapped.ClientModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.converters.UUIDConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
