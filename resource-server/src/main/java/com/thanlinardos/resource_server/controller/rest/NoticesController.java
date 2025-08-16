package com.thanlinardos.resource_server.controller.rest;

import com.thanlinardos.resource_server.model.mapped.NoticeDetailsModel;
import com.thanlinardos.resource_server.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class NoticesController {

    private final NoticeService noticeService;

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeDetailsModel>> getNoticesDetails() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(noticeService.getActiveNotices());
    }
}
