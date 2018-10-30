package com.chumbok.uaa.controller;

import com.chumbok.uaa.dto.request.UserCreateRequest;
import com.chumbok.uaa.dto.request.UserUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.UserResponse;
import com.chumbok.uaa.dto.response.UsersResponse;
import com.chumbok.uaa.exception.presentation.ValidationException;
import com.chumbok.uaa.service.UserService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Handles tenants users related APIs.
 */
@Slf4j
@RestController
@RequestMapping("/orgs/{orgId}/tenants/{tenantId}/users")
public class TenantUserController {

    private final UserService userService;

    /**
     * Construct UserController with UserService.
     *
     * @param userService the user service
     */
    public TenantUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all user page.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param pageable the pageable
     * @return the users response
     */
    @GetMapping
    public UsersResponse userList(@PathVariable String orgId, @PathVariable String tenantId,
                                  @PageableDefault(size = 10) Pageable pageable) {
        return userService.getUsers(orgId, tenantId, pageable);
    }

    /**
     * Get user by id.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param id       the id
     * @return UserResponse user response
     */
    @GetMapping("/{id}")
    public UserResponse userById(@PathVariable String orgId, @PathVariable String tenantId,
                                 @PathVariable String id) {
        return userService.getUser(orgId, tenantId, id);
    }

    /**
     * Create new user.
     *
     * @param orgId             the org id
     * @param tenantId          the tenant id
     * @param userCreateRequest the user create update request
     * @param bindingResult     the binding result
     * @return Id of created User.
     */
    @PostMapping
    public ResponseEntity<IdentityResponse> createUser(
            @PathVariable String orgId, @PathVariable String tenantId,
            @RequestBody @Valid UserCreateRequest userCreateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        return new ResponseEntity(userService.create(orgId, tenantId, userCreateRequest), HttpStatus.CREATED);
    }

    /**
     * Update user by id.
     *
     * @param orgId             the org id
     * @param tenantId          the tenant id
     * @param id                the id
     * @param userUpdateRequest the user update request
     * @param bindingResult     the binding result
     * @return the response entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String orgId, @PathVariable String tenantId,
                                           @PathVariable String id,
                                           @RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        userService.update(orgId, tenantId, id, userUpdateRequest);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete user by id with all tenants and users.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param id       the id
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String orgId, @PathVariable String tenantId,
                                           @PathVariable String id) {

        userService.delete(orgId, tenantId, id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
