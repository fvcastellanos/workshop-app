package net.cavitos.workshop.security.auth0;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Auth0Configuration {

    @Bean
    public Auth0AuthoritiesMapper auth0AuthoritiesMapper() {
        return new Auth0AuthoritiesMapper();
    }

    @Bean
    public Auth0LogoutHandler auth0LogoutHandler(@Value("${okta.oauth2.issuer}") final String issuer,
                                                 @Value("${okta.oauth2.client-id}") final String clientId,
                                                 final AuthenticationContext authenticationContext) {
        return new Auth0LogoutHandler(issuer, clientId, authenticationContext);
    }
}
