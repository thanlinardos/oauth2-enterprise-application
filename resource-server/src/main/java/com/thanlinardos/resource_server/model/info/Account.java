package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account implements Serializable {

    private List<AccountTransaction> accountTransactions;
    private long accountNumber;
    private String accountType;
    private String branchAddress;
}
