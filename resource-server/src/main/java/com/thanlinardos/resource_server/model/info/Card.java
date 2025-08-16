package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card implements Serializable {

    private String cardNumber;
    private String cardType;
    private Long totalLimit;
    private Long amountUsed;
    private Long availableAmount;
}
