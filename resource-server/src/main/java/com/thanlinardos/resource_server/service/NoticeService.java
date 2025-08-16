package com.thanlinardos.resource_server.service;

import com.thanlinardos.resource_server.model.entity.NoticeDetailsJpa;
import com.thanlinardos.resource_server.model.mapped.NoticeDetailsModel;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final EntityManager entityManager;

    @Getter
    @Setter
    private ConcurrentMapCache noticeCache = new ConcurrentMapCache("notices");

//    @Cacheable(cacheNames="notices", sync=true)
    public List<NoticeDetailsModel> getActiveNotices() {
        return entityManager
                .createQuery("from NoticeDetailsJpa n where CURDATE() BETWEEN n.noticBegDt and n.noticEndDt", NoticeDetailsJpa.class)
                .getResultList().stream()
                .map(NoticeDetailsModel::new)
                .toList();
    }
}
