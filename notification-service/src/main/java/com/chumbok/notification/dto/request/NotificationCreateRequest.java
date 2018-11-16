package com.chumbok.notification.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@Builder
public class NotificationCreateRequest {

    @NotNull
    @Size(min = 1, max = 500)
    private String content;

    @NotNull
    private Type type;

    private Map<String, String> additionalProperties;

    public enum Type {
        SUCCESS, WARNING, ERROR, CRITICAL
    }
}
