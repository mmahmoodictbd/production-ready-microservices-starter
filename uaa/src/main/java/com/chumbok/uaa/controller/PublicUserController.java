package com.chumbok.uaa.controller;

import com.chumbok.exception.presentation.ValidationException;
import com.chumbok.uaa.dto.request.PublicUserCreateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Handles public user creation API.
 */
@Slf4j
@AllArgsConstructor
@RestController
public class PublicUserController {

    private final UserService userService;

    /**
     * Create new user.
     *
     * @param publicUserCreateRequest the user public create request
     * @param bindingResult           the binding result
     * @return Id of created User.
     */
    @PostMapping("/public/users")
    public ResponseEntity<IdentityResponse> createPublicUser(
            @RequestBody @Valid PublicUserCreateRequest publicUserCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(userService.create(publicUserCreateRequest), HttpStatus.CREATED);
    }

    // TODO: complete this.
    @PatchMapping("/public/users/{id}")
    public void activateUser(@PathVariable String id, @RequestParam String activationToken) {
        userService.activatePublicUser(id, activationToken);
    }
}
