package com.chumbok.notification.controller;

import com.chumbok.exception.presentation.ValidationException;
import com.chumbok.notification.dto.request.NotificationCreateRequest;
import com.chumbok.notification.dto.response.IdentityResponse;
import com.chumbok.notification.dto.response.NotificationsResponse;
import com.chumbok.notification.service.NotificationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public NotificationsResponse notificationPage(Pageable pageable) {
        return notificationService.listByPage(pageable);
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createNotification(
            @RequestBody @Valid NotificationCreateRequest notificationCreateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(notificationService.create(notificationCreateRequest), HttpStatus.CREATED);
    }

}
