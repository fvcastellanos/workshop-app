package net.cavitos.workshop.security.auth0;

import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Auth0LogoutHandlerTest {

    private static final String ISSUER = "https://my-tenant.auth0.com/";
    private static final String CLIENT_ID = "test-client-id";
    private static final String PRINCIPAL_NAME = "test-user";

    @Mock
    private AuthenticationContext authenticationContext;

    @Mock
    private Authentication authentication;

    private Auth0LogoutHandler handler;

    @BeforeEach
    void setUp() {
        handler = new Auth0LogoutHandler(ISSUER, CLIENT_ID, authenticationContext);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logout_whenHttpSchemeAndNonLocalhostHost_shouldRedirectUsingHttpsScheme() {
        setUpRequestContext("http", "example.com", 80, "/");
        final var response = new MockHttpServletResponse();
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.of(PRINCIPAL_NAME));

        handler.logout(null, response, authentication);

        verify(authentication).setAuthenticated(false);
        assertThat(response.getRedirectedUrl())
                .contains(ISSUER + "v2/logout")
                .contains("client_id=" + CLIENT_ID)
                .contains("returnTo=https://example.com");
    }

    @Test
    void logout_whenHttpSchemeAndLocalhostHost_shouldKeepHttpScheme() {
        setUpRequestContext("http", "localhost", 8080, "/");
        final var response = new MockHttpServletResponse();
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.of(PRINCIPAL_NAME));

        handler.logout(null, response, authentication);

        verify(authentication).setAuthenticated(false);
        assertThat(response.getRedirectedUrl())
                .contains(ISSUER + "v2/logout")
                .contains("client_id=" + CLIENT_ID)
                .contains("returnTo=http://localhost");
    }

    @Test
    void logout_whenHttpsScheme_shouldNotChangeScheme() {
        setUpRequestContext("https", "example.com", 443, "/");
        final var response = new MockHttpServletResponse();
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.of(PRINCIPAL_NAME));

        handler.logout(null, response, authentication);

        verify(authentication).setAuthenticated(false);
        assertThat(response.getRedirectedUrl())
                .contains(ISSUER + "v2/logout")
                .contains("client_id=" + CLIENT_ID)
                .contains("returnTo=https://example.com");
    }

    @Test
    void logout_whenPrincipalNameNotPresent_shouldFallbackToAnonymousAndStillRedirect() {
        setUpRequestContext("https", "example.com", 443, "/");
        final var response = new MockHttpServletResponse();
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.empty());

        handler.logout(null, response, authentication);

        assertThat(response.getRedirectedUrl())
                .isNotNull()
                .contains(ISSUER + "v2/logout")
                .contains("client_id=" + CLIENT_ID);
    }

    @Test
    void logout_shouldBuildRedirectUrlWithIssuerClientIdAndReturnTo() {
        setUpRequestContext("https", "example.com", 443, "");
        final var response = new MockHttpServletResponse();
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.of(PRINCIPAL_NAME));

        handler.logout(null, response, authentication);

        assertThat(response.getRedirectedUrl())
                .isEqualTo(ISSUER + "v2/logout?client_id=" + CLIENT_ID + "&returnTo=https://example.com");
    }

    @Test
    void logout_whenSendRedirectThrowsIOException_shouldWrapInRuntimeException() throws IOException {
        setUpRequestContext("https", "example.com", 443, "/");
        final var mockResponse = mock(HttpServletResponse.class);
        final var ioException = new IOException("Network error");
        when(authenticationContext.getPrincipalName()).thenReturn(Optional.of(PRINCIPAL_NAME));
        doThrow(ioException).when(mockResponse).sendRedirect(anyString());

        assertThatThrownBy(() -> handler.logout(null, mockResponse, authentication))
                .isInstanceOf(RuntimeException.class)
                .hasCause(ioException);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void setUpRequestContext(final String scheme, final String host,
                                     final int port, final String requestUri) {
        final var request = new MockHttpServletRequest();
        request.setScheme(scheme);
        request.setServerName(host);
        request.setServerPort(port);
        request.setContextPath("");
        request.setRequestURI(requestUri);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}

