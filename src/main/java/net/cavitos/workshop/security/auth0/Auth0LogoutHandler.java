package net.cavitos.workshop.security.auth0;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

public class Auth0LogoutHandler extends VerticalLayout implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auth0LogoutHandler.class);

    private final String issuer;

    private final String clientId;

    private final transient AuthenticationContext authenticationContext;

    public Auth0LogoutHandler(@Value("${okta.oauth2.issuer}") final String issuer,
                              @Value("${okta.oauth2.client-id}") final String clientId,
                              final AuthenticationContext authenticationContext) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {

            final var principalName = authenticationContext.getPrincipalName()
                            .orElse("anonymous");

            LOGGER.info("Login out User: {}", principalName);

            authentication.setAuthenticated(false);

            final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .build()
                    .toUriString();

            final var redirectUrl = "%sv2/logout?client_id=%s&returnTo=%s"
                    .formatted(issuer, clientId, baseUrl);

            LOGGER.info("Redirect URL: {}", baseUrl);

            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
