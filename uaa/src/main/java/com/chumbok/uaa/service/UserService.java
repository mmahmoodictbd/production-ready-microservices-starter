package com.chumbok.uaa.service;

import com.chumbok.exception.presentation.ResourceNotFoundException;
import com.chumbok.exception.presentation.UnautherizedException;
import com.chumbok.exception.presentation.ValidationException;
import com.chumbok.security.util.SecurityUtil;
import com.chumbok.testable.common.UuidUtil;
import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.model.Role;
import com.chumbok.uaa.domain.model.Tenant;
import com.chumbok.uaa.domain.model.User;
import com.chumbok.uaa.domain.repository.OrgRepository;
import com.chumbok.uaa.domain.repository.RoleRepository;
import com.chumbok.uaa.domain.repository.TenantRepository;
import com.chumbok.uaa.domain.repository.UserRepository;
import com.chumbok.uaa.dto.request.PublicUserCreateRequest;
import com.chumbok.uaa.dto.request.UserCreateRequest;
import com.chumbok.uaa.dto.request.UserUpdateRequest;
import com.chumbok.uaa.dto.response.IdentityResponse;
import com.chumbok.uaa.dto.response.UserResponse;
import com.chumbok.uaa.dto.response.UsersResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chumbok.uaa.security.DefaultSecurityRoleConstants.ROLE_ADMIN;
import static com.chumbok.uaa.security.DefaultSecurityRoleConstants.ROLE_SUPERADMIN;

/**
 * User service for both Admin and Super Admin.
 */
@Service
@AllArgsConstructor
public class UserService {

    private final OrgRepository orgRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final UuidUtil uuidUtil;
    private final SecurityUtil securityUtil;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Gets user's page by super admin.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param pageable the pageable
     * @return the users page
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
    public UsersResponse getUsers(String orgId, String tenantId, Pageable pageable) {
        return getUsersResponse(userRepository.findAllByOrgIdAndTenantId(orgId, tenantId, pageable));
    }

    /**
     * Gets user's page by admin.
     *
     * @param pageable the pageable
     * @return the users page
     */
    @Secured(ROLE_ADMIN)
    @Transactional(readOnly = true)
    public UsersResponse getUsers(Pageable pageable) {
        return getUsersResponse(userRepository.findAll(pageable));
    }

    /**
     * Gets user by super admin.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param id       the id
     * @return the user
     */
    @Secured(ROLE_SUPERADMIN)
    @Transactional(readOnly = true)
    public UserResponse getUser(String orgId, String tenantId, String id) {
        return getUserResponse(userRepository.findByOrgIdAndTenantIdAndId(orgId, tenantId, id));
    }

    /**
     * Gets user by admin.
     *
     * @param id the id
     * @return the user
     */
    @Secured(ROLE_ADMIN)
    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {
        return getUserResponse(userRepository.findById(id));
    }

    /**
     * Create User by super user.
     *
     * @param orgId             the org id
     * @param tenantId          the tenant id
     * @param userCreateRequest the user create request
     * @return the user's id
     */
    @Secured(ROLE_SUPERADMIN)
    public IdentityResponse create(String orgId, String tenantId, UserCreateRequest userCreateRequest) {

        Optional<Org> orgOptional = orgRepository.findById(orgId);
        if (!orgOptional.isPresent()) {
            throw new ValidationException("Org not found.");
        }

        Optional<Tenant> tenantOptional = tenantRepository.findById(tenantId);
        if (!tenantOptional.isPresent()) {
            throw new ValidationException("Tenant not found.");
        }

        return getIdentityResponse(userCreateRequest, orgOptional.get(), tenantOptional.get());
    }

    /**
     * Create User by admin.
     *
     * @param userCreateRequest the user create request
     * @return the user's id
     */
    @Secured(ROLE_ADMIN)
    public IdentityResponse create(UserCreateRequest userCreateRequest) {

        Optional<SecurityUtil.AuthenticatedUser> authenticatedUser = securityUtil.getAuthenticatedUser();
        if (!authenticatedUser.isPresent()) {
            throw new UnautherizedException("User not authenticated.");
        }

        Org org = orgRepository.getOne(authenticatedUser.get().getOrg());
        Tenant tenant = tenantRepository.getOne(authenticatedUser.get().getTenant());

        return getIdentityResponse(userCreateRequest, org, tenant);
    }

    /**
     * Create public User.
     *
     * @param publicUserCreateRequest the user create request
     * @return the user's id
     */
    public IdentityResponse create(PublicUserCreateRequest publicUserCreateRequest) {

        if (!orgRepository.existsByOrg(publicUserCreateRequest.getOrg())) {
            throw new ValidationException("Org not found.");
        }

        if (!tenantRepository.existsByOrgOrgAndTenant(publicUserCreateRequest.getOrg(),
                publicUserCreateRequest.getTenant())) {
            throw new ValidationException("Tenant not found.");
        }

        boolean isExist = isExist(publicUserCreateRequest.getOrg(), publicUserCreateRequest.getTenant(),
                publicUserCreateRequest.getUsername());
        if (isExist) {
            throw new ValidationException("Username '" + publicUserCreateRequest.getUsername() + "' already taken.");
        }

        String uuid = uuidUtil.getUuid();

        User user = new User();
        user.setId(uuid);
        user.setOrg(orgRepository.getByOrg(publicUserCreateRequest.getOrg()));
        user.setTenant(tenantRepository.getByTenant(publicUserCreateRequest.getTenant()));
        user.setUsername(publicUserCreateRequest.getUsername());
        user.setDisplayName(publicUserCreateRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(publicUserCreateRequest.getPassword()));
        user.setEnabled(publicUserCreateRequest.isEnabled());
        user.setRoles(getRolesOrThrow(publicUserCreateRequest.getRoles()));

        userRepository.saveAndFlush(user);

        return new IdentityResponse(uuid);
    }

    public boolean isExist(String org, String tenant, String username) {
        return userRepository.isExist(org, tenant, username);
    }

    /**
     * Update User by super admin.
     *
     * @param orgId             the org id
     * @param tenantId          the tenant id
     * @param userId            the user id
     * @param userUpdateRequest the user update request
     */
    @Secured(ROLE_SUPERADMIN)
    public void update(String orgId, String tenantId, String userId, UserUpdateRequest userUpdateRequest) {
        updateUser(userUpdateRequest, userRepository.findByOrgIdAndTenantIdAndId(orgId, tenantId, userId));
    }

    /**
     * Update User by admin.
     *
     * @param userId            the user id
     * @param userUpdateRequest the user update request
     */
    @Secured(ROLE_ADMIN)
    public void update(String userId, UserUpdateRequest userUpdateRequest) {
        updateUser(userUpdateRequest, userRepository.findById(userId));
    }

    /**
     * Delete User by super admin.
     *
     * @param orgId    the org id
     * @param tenantId the tenant id
     * @param userId   the user id
     */
    @Secured(ROLE_SUPERADMIN)
    public void delete(String orgId, String tenantId, String userId) {
        deleteUser(userId, userRepository.findByOrgIdAndTenantIdAndId(orgId, tenantId, userId));
    }

    /**
     * Delete User by admin.
     *
     * @param userId the user id
     */
    @Secured(ROLE_ADMIN)
    public void delete(String userId) {
        deleteUser(userId, userRepository.findById(userId));
    }

    // TODO: validate activation token and enable user
    public void activatePublicUser(String userId, String activationToken) {
    }

    private IdentityResponse getIdentityResponse(UserCreateRequest userCreateRequest, Org org, Tenant tenant) {

        boolean isExist = userRepository.isExist(org.getOrg(), tenant.getTenant(), userCreateRequest.getUsername());
        if (isExist) {
            throw new ValidationException("Username '" + userCreateRequest.getUsername() + "' already taken.");
        }

        String uuid = uuidUtil.getUuid();

        User user = new User();
        user.setId(uuid);
        user.setOrg(org);
        user.setTenant(tenant);

        mapUserCreateRequestToUserEntity(userCreateRequest, user);

        userRepository.saveAndFlush(user);
        return new IdentityResponse(uuid);
    }

    private void mapUserCreateRequestToUserEntity(UserCreateRequest userCreateRequest, User user) {

        user.setUsername(userCreateRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userCreateRequest.getPassword()));
        mapUserUpdateRequestToUserEntity(userCreateRequest, user);
    }

    private void mapUserUpdateRequestToUserEntity(UserUpdateRequest userUpdateRequest, User user) {

        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setDisplayName(userUpdateRequest.getDisplayName());
        user.setImageUrl(userUpdateRequest.getImageUrl());
        user.setTimezoneId(userUpdateRequest.getTimezoneId());
        user.setPreferredLanguage(userUpdateRequest.getPreferredLanguage());
        user.setRoles(getRolesOrThrow(userUpdateRequest.getRoles()));
        user.setEnabled(userUpdateRequest.isEnabled());
    }

    private UserResponse getUserResponse(Optional<User> userOptional) {

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User not found.");
        }

        return buildUserResponse(userOptional.get());
    }

    private UsersResponse getUsersResponse(Page<User> userPage) {

        long totalElements = userPage.getTotalElements();
        int totalPage = userPage.getTotalPages();
        int size = userPage.getSize();
        int page = userPage.getNumber();

        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userPage.getContent()) {
            userResponseList.add(buildUserResponse(user));
        }

        return UsersResponse.builder()
                .items(userResponseList)
                .page(page)
                .size(size)
                .totalPages(totalPage)
                .totalElements(totalElements)
                .build();
    }

    private UserResponse buildUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .imageUrl(user.getImageUrl())
                .timezoneId(user.getTimezoneId())
                .preferredLanguage(user.getPreferredLanguage())
                .roles(user.getRoles().stream().map(role -> role.getRole()).collect(Collectors.toSet()))
                .enabled(user.isEnabled())
                .build();
    }

    private void updateUser(UserUpdateRequest userUpdateRequest, Optional<User> userOptional) {

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = userOptional.get();
        mapUserUpdateRequestToUserEntity(userUpdateRequest, user);
        userRepository.save(user);
    }

    private void deleteUser(String id, Optional<User> userOptional) {

        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User not found.");
        }

        userRepository.deleteById(id);
    }

    private Set<Role> getRolesOrThrow(Set<String> roles2) {
        Set<Role> roles = new HashSet<>();
        for (String role : roles2) {
            Optional<Role> roleOptional = roleRepository.findByRole(role);
            if (!roleOptional.isPresent()) {
                throw new ValidationException("Role '" + role + "' does not exist.");
            }
            roles.add(roleOptional.get());
        }
        return roles;
    }
}
