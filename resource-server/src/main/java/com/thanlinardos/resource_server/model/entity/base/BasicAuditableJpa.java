package com.thanlinardos.resource_server.model.entity.base;

import com.thanlinardos.resource_server.model.mapped.base.BasicAuditableModel;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BasicAuditableJpa extends BasicIdJpa {

    @Column(name = "created_at", nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "created_by", nullable = false, updatable = false)
    @ColumnDefault("'system'")
    @CreatedBy
    @Builder.Default
    private String createdBy = "system";
    @Column(name = "updated_at", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", nullable = false)
    @ColumnDefault("'system'")
    @LastModifiedBy
    @Builder.Default
    private String updatedBy = "system";

    public void setTrackedProperties(BasicAuditableModel model) {
            setCreatedAt(model.getCreatedAt());
            setCreatedBy(model.getCreatedBy());
            setUpdatedAt(model.getUpdatedAt());
            setUpdatedBy(model.getUpdatedBy());
    }

    public void setTrackedPropertiesWithLink(BasicAuditableModel model) {
        setId(model.getId());
        setTrackedProperties(model);
    }
}
