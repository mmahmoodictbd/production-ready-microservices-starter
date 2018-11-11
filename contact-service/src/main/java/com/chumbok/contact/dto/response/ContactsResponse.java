package com.chumbok.contact.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ContactsResponse {

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<ContactResponse> items;
}
