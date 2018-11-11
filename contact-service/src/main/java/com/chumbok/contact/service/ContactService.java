package com.chumbok.contact.service;

import com.chumbok.contact.domain.model.Contact;
import com.chumbok.contact.domain.repository.ContactRepository;
import com.chumbok.contact.dto.request.ContactCreateUpdateRequest;
import com.chumbok.contact.dto.response.ContactResponse;
import com.chumbok.contact.dto.response.ContactsResponse;
import com.chumbok.contact.dto.response.IdentityResponse;
import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.UuidUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles Contact related APIs
 */
@Service
public class ContactService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ContactRepository contactRepository;
    private final SecurityUtil securityUtil;
    private final UuidUtil uuidUtil;

    /**
     * Instantiates a new Contact service.
     *
     * @param contactRepository the contact repository
     * @param securityUtil      the security util
     * @param uuidUtil          the uuid util
     */
    public ContactService(ContactRepository contactRepository, SecurityUtil securityUtil, UuidUtil uuidUtil) {
        this.contactRepository = contactRepository;
        this.securityUtil = securityUtil;
        this.uuidUtil = uuidUtil;
    }

    /**
     * Gets pageable contacts.
     *
     * @param pageable the pageable
     * @return the contact page
     */
    @Transactional(readOnly = true)
    public ContactsResponse getContacts(Pageable pageable) {

        Page<Contact> contactPage = contactRepository.findAllByCreatedBy(getLoggedInUsername(), pageable);

        long totalElements = contactPage.getTotalElements();
        int totalPage = contactPage.getTotalPages();
        int size = contactPage.getSize();
        int page = contactPage.getNumber();

        List<ContactResponse> contactResponseList = new ArrayList<>();
        for (Contact contact : contactPage.getContent()) {
            contactResponseList.add(buildContactResponse(contact));
        }

        return ContactsResponse.builder()
                .items(contactResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }


    /**
     * Gets contact.
     *
     * @param contactId the contactId
     * @return the contact
     */
    @Transactional(readOnly = true)
    public ContactResponse getContact(String contactId) {

        Contact contact = getContactOrThrowNotFoundException(contactId);
        return buildContactResponse(contact);
    }

    /**
     * Create Contact.
     */
    public IdentityResponse createContact(ContactCreateUpdateRequest contactCreateUpdateRequest) {

        String uuid = uuidUtil.getUuid();

        Contact contact = new Contact();
        contact.setId(uuid);
        contact.setDisplayName(contactCreateUpdateRequest.getDisplayName());
        contact.setFirstName(contactCreateUpdateRequest.getFirstName());
        contact.setLastName(contactCreateUpdateRequest.getLastName());
        contact.setEmail1(contactCreateUpdateRequest.getEmail1());
        contact.setEmail2(contactCreateUpdateRequest.getEmail2());
        contact.setMobile1(contactCreateUpdateRequest.getMobile1());
        contact.setMobile2(contactCreateUpdateRequest.getMobile2());
        contact.setPhone(contactCreateUpdateRequest.getPhone());
        contact.setAddressLine1(contactCreateUpdateRequest.getAddressLine1());
        contact.setAddressLine2(contactCreateUpdateRequest.getAddressLine2());
        contact.setAddressLine3(contactCreateUpdateRequest.getAddressLine3());
        contact.setAddressLine4(contactCreateUpdateRequest.getAddressLine4());
        contact.setCity(contactCreateUpdateRequest.getCity());
        contact.setZip(contactCreateUpdateRequest.getZip());
        contact.setBirthday(LocalDate.parse(contactCreateUpdateRequest.getBirthday(), formatter));
        contact.setImageUrl(contactCreateUpdateRequest.getImageUrl());
        contact.setAdditionalProperties(contactCreateUpdateRequest.getAdditionalProperties());

        contactRepository.saveAndFlush(contact);
        return new IdentityResponse(uuid);
    }

    /**
     * Delete Contact.
     */
    public void deleteContact(String contactId) {

        Contact contact = getContactOrThrowNotFoundException(contactId);
        contactRepository.deleteById(contact.getId());
    }

    private Contact getContactOrThrowNotFoundException(String contactId) {

        Optional<Contact> contactOptional = contactRepository.findByIdAndCreatedBy(contactId, getLoggedInUsername());

        if (!contactOptional.isPresent()) {
            throw new ResourceNotFoundException("Contact not found.");
        }

        return contactOptional.get();
    }

    private ContactResponse buildContactResponse(Contact contact) {

        return ContactResponse.builder()
                .id(contact.getId())
                .displayName(contact.getDisplayName())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email1(contact.getEmail1())
                .email2(contact.getEmail2())
                .mobile1(contact.getMobile1())
                .mobile2(contact.getMobile2())
                .phone(contact.getPhone())
                .addressLine1(contact.getAddressLine1())
                .addressLine2(contact.getAddressLine2())
                .addressLine3(contact.getAddressLine3())
                .addressLine4(contact.getAddressLine4())
                .city(contact.getCity())
                .zip(contact.getZip())
                .birthday(contact.getBirthday().format(formatter))
                .imageUrl(contact.getImageUrl())
                .additionalProperties(contact.getAdditionalProperties())
                .build();
    }

    private String getLoggedInUsername() {
        return securityUtil.getAuthenticatedUser().get().getUsername();
    }
}
