package com.chumbok.uaa.domain.repository;

import com.chumbok.uaa.Application;
import com.chumbok.uaa.domain.model.Org;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("it")
public class DataJpaAuditIT {

    @Autowired
    private OrgRepository orgRepository;

    @Test
    public void testIfAuditFieldsPopulated() {

        // Given

        Org org = new Org("Org");

        // When
        Org persistedOrg = orgRepository.save(org);

        // Then
        assertNotNull(persistedOrg.getCreatedBy());
        assertNotNull(persistedOrg.getCreatedAt());
        assertNotNull(persistedOrg.getUpdatedBy());
        assertNotNull(persistedOrg.getUpdatedAt());
    }

}
