package com.chumbok.uaa.security;

import com.chumbok.security.util.EncryptionKeyUtil;
import com.chumbok.testable.common.DateUtil;
import com.chumbok.uaa.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.json.JSONException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthTokenBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final AuthJwtProperties authJwtPropertiesMock;
    private final EncryptionKeyUtil encryptionKeyUtilMock;
    private final DateUtil dateUtilMock;
    private final JwtUtil jwtUtilMock;

    private AuthTokenBuilder authTokenBuilder;

    public AuthTokenBuilderTest() {
        this.authJwtPropertiesMock = mock(AuthJwtProperties.class);
        this.encryptionKeyUtilMock = mock(EncryptionKeyUtil.class);
        this.dateUtilMock = mock(DateUtil.class);
        this.jwtUtilMock = mock(JwtUtil.class);
        this.authTokenBuilder = new AuthTokenBuilder(authJwtPropertiesMock, encryptionKeyUtilMock,
                dateUtilMock, jwtUtilMock);
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfPrincipleIsNullOrEmpty() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(null);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Authentication principle can not be null or empty.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfOrgTenantAndUsernameCanNotBeSeparatedFromPrinciple() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn("HelloUser");

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Authentication principle[HelloUser] should contain org, tenant and username.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfOrgIsEmptyOrNull() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(" " + Character.LINE_SEPARATOR + "HelloTenant"
                + Character.LINE_SEPARATOR + "HelloUser");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Authentication principle[ " + Character.LINE_SEPARATOR + "HelloTenant"
                + Character.LINE_SEPARATOR
                + "HelloUser] does not contain org.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfTenantIsEmptyOrNull() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn("HelloOrg" + Character.LINE_SEPARATOR + ""
                + Character.LINE_SEPARATOR + "HelloUser");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Authentication principle[HelloOrg" + Character.LINE_SEPARATOR + ""
                + Character.LINE_SEPARATOR
                + "HelloUser] does not contain tenant.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfUsernameIsEmptyOrNull() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn("HelloOrg" + Character.LINE_SEPARATOR + "HelloTenant"
                + Character.LINE_SEPARATOR + " ");

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Authentication principle[HelloOrg" + Character.LINE_SEPARATOR
                + "HelloTenant" + Character.LINE_SEPARATOR + " ] does not contain username.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfAuthoritiesIsEmptyOrNull() {

        // Given

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn("HelloOrg"
                + Character.LINE_SEPARATOR
                + "HelloTenant"
                + Character.LINE_SEPARATOR
                + "HelloUser");
        when(authenticationMock.getAuthorities()).thenReturn(Collections.emptyList());

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Authentication principle[HelloOrg"
                + Character.LINE_SEPARATOR
                + "HelloTenant"
                + Character.LINE_SEPARATOR
                + "HelloUser] does not contain authorities.");

        // When
        authTokenBuilder.createAccessToken(authenticationMock);

        // Then
        // Expect test to be passed.

    }

    @Test
    public void shouldReturnAccessTokenOnSuccessfulAuthentication() throws NoSuchAlgorithmException, JSONException {

        // Given

        ArgumentCaptor<Claims> claimsCaptor = ArgumentCaptor.forClass(Claims.class);
        ArgumentCaptor<String> issuerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Date> issueDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<Date> expirationDateCaptor = ArgumentCaptor.forClass(Date.class);
        ArgumentCaptor<PrivateKey> privateKeyCaptor = ArgumentCaptor.forClass(PrivateKey.class);

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn("HelloOrg"
                + Character.LINE_SEPARATOR
                + "HelloTenant"
                + Character.LINE_SEPARATOR
                + "HelloUser");

        Collection authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_HELLO"));
        when(authenticationMock.getAuthorities()).thenReturn(authorities);

        LocalDateTime localDateTime = LocalDateTime.now();
        when(dateUtilMock.getCurrentLocalDateTime()).thenReturn(localDateTime);

        when(authJwtPropertiesMock.getTokenExpirationTimeInSecond()).thenReturn(5);
        when(authJwtPropertiesMock.getTokenIssuer()).thenReturn("ME!");
        when(authJwtPropertiesMock.getTokenSigningPrivateKeyPath()).thenReturn("privateKeyPath");

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(4096);
        PrivateKey privateKey = keyGen.genKeyPair().getPrivate();
        when(encryptionKeyUtilMock.loadPrivateKey("privateKeyPath")).thenReturn(privateKey);

        when(jwtUtilMock.getJwts(any(), any(), any(), any(), any())).thenReturn("mockJwtToken");

        // When
        String accessToken = authTokenBuilder.createAccessToken(authenticationMock);

        // Then

        assertEquals("mockJwtToken", accessToken);

        verify(jwtUtilMock).getJwts(claimsCaptor.capture(), issuerCaptor.capture(), issueDateCaptor.capture(),
                expirationDateCaptor.capture(), privateKeyCaptor.capture());
        assertEquals("HelloUser", claimsCaptor.getValue().getSubject());
        assertEquals("HelloOrg", claimsCaptor.getValue().get("org"));
        assertEquals("HelloTenant", claimsCaptor.getValue().get("tenant"));
        assertEquals("[ROLE_HELLO]", claimsCaptor.getValue().get("scopes").toString());
        assertEquals("ME!", issuerCaptor.getValue());

        assertEquals(localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond(),
                issueDateCaptor.getValue().getTime() / 1000);
        assertEquals(localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond() + 5,
                expirationDateCaptor.getValue().getTime() / 1000);

        assertEquals(privateKey, privateKeyCaptor.getValue());
    }

}