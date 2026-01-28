package com.thanlinardos.resource_server.repository;

import com.thanlinardos.resource_server.model.entity.contact.NoticeDetailsJpa;
import com.thanlinardos.resource_server.repository.api.CustomNoticeRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NoticeRepositoryImpl implements CustomNoticeRepository {

    private final EntityManager entityManager;

    @Override
    public List<NoticeDetailsJpa> getActiveNotices() {
        return entityManager
                .createQuery("from NoticeDetailsJpa n where CURDATE() BETWEEN n.noticBegDt and n.noticEndDt", NoticeDetailsJpa.class)
                .getResultList();
    }
}
