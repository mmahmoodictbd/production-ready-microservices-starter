package com.chumbok.notification.service;

import com.chumbok.notification.domain.model.Notification;
import com.chumbok.notification.domain.model.NotificationType;
import com.chumbok.notification.domain.repository.NotificationRepository;
import com.chumbok.notification.dto.request.NotificationCreateRequest;
import com.chumbok.notification.dto.response.IdentityResponse;
import com.chumbok.notification.dto.response.NotificationResponse;
import com.chumbok.notification.dto.response.NotificationsResponse;
import com.chumbok.testable.common.UuidUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UuidUtil uuidUtil;

    public NotificationService(NotificationRepository notificationRepository, UuidUtil uuidUtil) {
        this.notificationRepository = notificationRepository;
        this.uuidUtil = uuidUtil;
    }

    public NotificationsResponse listByPage(Pageable pageable) {

        Page<Notification> notificationPage = notificationRepository.findAll(pageable);

        long totalElements = notificationPage.getTotalElements();
        int totalPage = notificationPage.getTotalPages();
        int size = notificationPage.getSize();
        int page = notificationPage.getNumber();

        List<NotificationResponse> notificationResponseList = new ArrayList<>();
        for (Notification notification : notificationPage.getContent()) {
            NotificationResponse notificationResponse = new NotificationResponse();
            notificationResponse.setId(notification.getId());
            notificationResponse.setContent(notification.getContent());
            notificationResponse.setType(NotificationResponse.Type.valueOf(notification.getType().toString()));
            notificationResponse.setCreatedAt(notification.getCreatedAt());
            notificationResponse.setAdditionalProperties(notification.getAdditionalProperties());
            notificationResponseList.add(notificationResponse);
        }

        return NotificationsResponse.builder()
                .items(notificationResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();

    }

    public IdentityResponse create(NotificationCreateRequest notificationCreateRequest) {

        String uuid = uuidUtil.getUuid();

        Notification notification = new Notification();
        notification.setId(uuid);
        notification.setContent(notificationCreateRequest.getContent());
        notification.setType(NotificationType.valueOf(notificationCreateRequest.getType().toString()));
        notification.setAdditionalProperties(notificationCreateRequest.getAdditionalProperties());
        notificationRepository.save(notification);

        return new IdentityResponse(uuid);
    }

}
