package com.thanlinardos.resource_server.model.entity;

import com.thanlinardos.spring_enterprise_library.model.entity.base.BasicIdJpa;
import jakarta.persistence.*;
import lombok.*;
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
