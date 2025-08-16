package com.thanlinardos.resource_server.model.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDetails implements Serializable {

    private String noticeSummary;
    private String noticeDetail;
    private LocalDate noticBegDt;
    private LocalDate noticEndDt;
}
