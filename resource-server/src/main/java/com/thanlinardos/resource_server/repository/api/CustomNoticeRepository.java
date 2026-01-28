package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.contact.NoticeDetailsJpa;

import java.util.List;

public interface CustomNoticeRepository {

    List<NoticeDetailsJpa> getActiveNotices();
}
