package com.chumbok.contact.controller;

import com.chumbok.contact.dto.request.ContactCreateUpdateRequest;
import com.chumbok.contact.dto.response.ContactResponse;
import com.chumbok.contact.dto.response.ContactsResponse;
import com.chumbok.contact.dto.response.IdentityResponse;
import com.chumbok.contact.service.ContactService;
import com.chumbok.exception.presentation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Handles contacts related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    /**
     * Construct ContactController with ContactService.
     *
     * @param contactService the contact service
     */
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Get contact page.
     *
     * @param pageable the pageable
     * @return the contacts response
     */
    @GetMapping
    public ContactsResponse contactList(@PageableDefault(size = 10) Pageable pageable) {
        return contactService.getContacts(pageable);
    }

    /**
     * Get contact by id.
     *
     * @param id the id
     * @return ContactResponse contact response
     */
    @GetMapping("/{id}")
    public ContactResponse contactById(@PathVariable String id) {
        return contactService.getContact(id);
    }

    /**
     * Create new contact.
     *
     * @param contactCreateUpdateRequest the contact create update request
     * @param bindingResult              the binding result
     * @return Id of created Contact.
     */
    @PostMapping
    public ResponseEntity<IdentityResponse> createContact(
            @RequestBody @Valid ContactCreateUpdateRequest contactCreateUpdateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(contactService.createContact(contactCreateUpdateRequest), HttpStatus.CREATED);
    }

    /**
     * Delete contact by id with all tenants and users.
     *
     * @param id the id
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {

        contactService.deleteContact(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
