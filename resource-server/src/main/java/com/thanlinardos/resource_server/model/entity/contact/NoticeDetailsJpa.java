package com.thanlinardos.resource_server.model.entity.contact;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "notice_details")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class NoticeDetailsJpa extends BasicIdJpa {

    private String noticeSummary;
    private String noticeDetails;
    private LocalDate noticBegDt;
    private LocalDate noticEndDt;
}
