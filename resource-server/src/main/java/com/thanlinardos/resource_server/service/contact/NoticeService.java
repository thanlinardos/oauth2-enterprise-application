package com.thanlinardos.resource_server.service.contact;

import com.thanlinardos.resource_server.model.mapped.NoticeDetailsModel;
import com.thanlinardos.resource_server.repository.api.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeDetailsModel> getActiveNotices() {
        return noticeRepository.getActiveNotices().stream()
                .map(NoticeDetailsModel::new)
                .toList();
    }
}
