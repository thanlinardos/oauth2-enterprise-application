package com.thanlinardos.resource_server.model.entity.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BasicIdJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
