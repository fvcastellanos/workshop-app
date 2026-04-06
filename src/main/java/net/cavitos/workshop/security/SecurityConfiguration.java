package net.cavitos.workshop.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import net.cavitos.workshop.model.repository.ApplicationUserRepository;
import net.cavitos.workshop.security.service.DefaultUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

    @Bean
    public DefaultUserService databaseUserService(final ApplicationUserRepository applicationUserRepository,
                                                  final AuthenticationContext authenticationContext) {

        return new DefaultUserService(applicationUserRepository, authenticationContext);
    }
}
