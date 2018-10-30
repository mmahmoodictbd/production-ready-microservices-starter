package com.chumbok.uaa.domain.repository;

import com.chumbok.security.util.SecurityUtil;
import com.chumbok.security.util.SecurityUtil.AuthenticatedUser;
import com.chumbok.uaa.Application;
import com.chumbok.uaa.domain.model.Org;
import com.chumbok.uaa.domain.model.Tenant;
import com.chumbok.uaa.domain.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class, DataJpaAuditIT.Config.class})
@ActiveProfiles("it")
public class UserRepositoryIT {

    private Org org1;
    private Tenant org1Tenant1;
    private Tenant org1Tenant2;

    private Org org2;
    private Tenant org2Tenant1;
    private Tenant org2Tenant2;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init(){

        org1 = new Org();
        org1.setId("uuid1");
        org1.setOrg("Org1");
        org1 = orgRepository.saveAndFlush(org1);

        org1Tenant1 = new Tenant("Tenant1");
        org1Tenant1.setId("uuid2");
        org1Tenant1.setOrg(org1);
        org1Tenant1 = tenantRepository.saveAndFlush(org1Tenant1);

        org1Tenant2 = new Tenant("Tenant2");
        org1Tenant2.setId("uuid3");
        org1Tenant2.setOrg(org1);
        org1Tenant2 = tenantRepository.saveAndFlush(org1Tenant2);

        org2 = new Org("Org2");
        org2.setId("uuid12");
        org2 = orgRepository.saveAndFlush(org2);

        org2Tenant1 = new Tenant("Tenant1");
        org2Tenant1.setId("uuid4");
        org2Tenant1.setOrg(org2);
        org2Tenant1 = tenantRepository.saveAndFlush(org1Tenant1);

        org2Tenant2 = new Tenant("Tenant2");
        org2Tenant2.setId("uuid5");
        org2Tenant2.setOrg(org1);
        org2Tenant2 = tenantRepository.saveAndFlush(org1Tenant2);
    }

    @After
    public void tearDown(){
        userRepository.deleteAll();
        tenantRepository.deleteAll();
        orgRepository.deleteAll();
    }

    @Test
    public void shouldThrowExceptionWhenSameTenantHaveSameUserExist() {

        // Given

        expectedException.expect(DataIntegrityViolationException.class);

        User user1 = new User();
        user1.setId("uuid6");
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("SomeUserPassHash");
        user1.setDisplayName("SomeDisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setId("uuid7");
        user2.setOrg(org1);
        user2.setTenant(org1Tenant1);
        user2.setUsername("SameUsername");
        user2.setPassword("DifferentUserPassHash");
        user2.setDisplayName("DifferentDisplayName");
        userRepository.saveAndFlush(user2);

        // Then
        // Should just pass because expected exception raised
    }

    @Test
    public void shouldAbleToSaveUserWithDifferentTenantButSameUsername() {

        // Given

        User user1 = new User();
        user1.setId("uuid8");
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        user1 = userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setId("uuid9");
        user2.setOrg(org1);
        user2.setTenant(org1Tenant2);
        user2.setUsername("SameUsername");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        user2 = userRepository.saveAndFlush(user2);

        // Then

        assertNotNull(user1.getCreatedAt());
        assertNotNull(user2.getCreatedAt());
    }

    @Test
    public void shouldAbleToSaveUserWithDifferentOrgButSameUsername() {

        // Given

        User user1 = new User();
        user1.setId("uuid10");
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        user1 = userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setId("uuid11");
        user2.setOrg(org2);
        user2.setTenant(org2Tenant1);
        user2.setUsername("SameUsername");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        user2 = userRepository.saveAndFlush(user2);

        // Then

        assertNotNull(user1.getCreatedAt());
        assertNotNull(user2.getCreatedAt());
    }
}
