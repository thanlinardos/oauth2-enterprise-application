package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.resource_server.model.entity.base.BasicManyToOneAccountIdJpa;
import com.thanlinardos.resource_server.model.mapped.CardModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.thanlinardos.spring_enterprise_library.spring_cloud_security.utils.EntityUtils.buildEntityWithIdOrNull;

@Entity
@Table(name = "cards")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class CardJpa extends BasicManyToOneAccountIdJpa {

    private String cardNumber;
    private String cardType;
    private Long totalLimit;
    private Long amountUsed;
    private Long availableAmount;

    public static CardJpa fromModel(CardModel model) {
        return builder()
                .cardNumber(model.getCardNumber())
                .cardType(model.getCardType())
                .totalLimit(model.getTotalLimit())
                .amountUsed(model.getAmountUsed())
                .availableAmount(model.getAvailableAmount())
                .account(buildEntityWithIdOrNull(model.getAccountId()))
                .build();
    }
}
