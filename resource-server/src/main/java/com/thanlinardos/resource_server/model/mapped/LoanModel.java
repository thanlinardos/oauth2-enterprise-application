package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.LoanJpa;
import com.thanlinardos.resource_server.model.mapped.base.BasicOwnedIdModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LoanModel extends BasicOwnedIdModel implements Serializable {

    private LocalDate startDt;
    private String loanType;
    private Long totalLoan;
    private Long amountPaid;
    private Long outstandingAmount;

    public LoanModel() {
        super();
    }

    public LoanModel(LoanJpa entity) {
        super(entity);
        this.setStartDt(entity.getStartDt());
        this.setLoanType(entity.getLoanType());
        this.setTotalLoan(entity.getTotalLoan());
        this.setAmountPaid(entity.getAmountPaid());
        this.setOutstandingAmount(entity.getOutstandingAmount());
        this.setOwner(new OwnerModel(entity.getOwner()));
    }
}
