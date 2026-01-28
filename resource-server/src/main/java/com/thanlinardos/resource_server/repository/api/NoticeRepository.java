package com.thanlinardos.resource_server.repository.api;

import com.thanlinardos.resource_server.model.entity.contact.NoticeDetailsJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<NoticeDetailsJpa, Long>, CustomNoticeRepository {
}
