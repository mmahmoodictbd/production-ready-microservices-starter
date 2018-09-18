package com.chumbok.uaa.domain.repository;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
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

        org1 = orgRepository.saveAndFlush(new Org("Org1"));

        org1Tenant1 = new Tenant("Tenant1");
        org1Tenant1.setOrg(org1);
        org1Tenant1 = tenantRepository.saveAndFlush(org1Tenant1);

        org1Tenant2 = new Tenant("Tenant2");
        org1Tenant2.setOrg(org1);
        org1Tenant2 = tenantRepository.saveAndFlush(org1Tenant2);

        org2 = orgRepository.saveAndFlush(new Org("Org2"));

        org2Tenant1 = new Tenant("Tenant1");
        org2Tenant1.setOrg(org2);
        org2Tenant1 = tenantRepository.saveAndFlush(org1Tenant1);

        org2Tenant2 = new Tenant("Tenant2");
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
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("SomeUserPassHash");
        user1.setDisplayName("SomeDisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
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
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setOrg(org1);
        user2.setTenant(org1Tenant2);
        user2.setUsername("SameUsername");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user2);

        // Then

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
    }

    @Test
    public void shouldAbleToSaveUserWithDifferentOrgButSameUsername() {

        // Given

        User user1 = new User();
        user1.setOrg(org1);
        user1.setTenant(org1Tenant1);
        user1.setUsername("SameUsername");
        user1.setPassword("UserPassHash");
        user1.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user1);

        // When

        User user2 = new User();
        user2.setOrg(org2);
        user2.setTenant(org2Tenant1);
        user2.setUsername("SameUsername");
        user2.setPassword("UserPassHash");
        user2.setDisplayName("DisplayName");
        userRepository.saveAndFlush(user2);

        // Then

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
    }

}
