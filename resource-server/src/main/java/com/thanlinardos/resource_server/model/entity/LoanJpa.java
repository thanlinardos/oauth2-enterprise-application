package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicManyToOneOwnedIdJpa;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class LoanJpa extends BasicManyToOneOwnedIdJpa {

    private LocalDate startDt;
    private String loanType;
    private Long totalLoan;
    private Long amountPaid;
    private Long outstandingAmount;
}
