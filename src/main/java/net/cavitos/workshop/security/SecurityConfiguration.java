package net.cavitos.workshop.security;

import net.cavitos.workshop.model.repository.ApplicationUserRepository;
import net.cavitos.workshop.security.service.DatabaseUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

    @Bean
    public DatabaseUserService databaseUserService(final ApplicationUserRepository applicationUserRepository) {

        return new DatabaseUserService(applicationUserRepository);
    }
}
