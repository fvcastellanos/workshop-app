package net.cavitos.workshop.security;

import net.cavitos.workshop.model.repository.UserRepository;
import net.cavitos.workshop.security.service.DatabaseUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

    @Bean
    public DatabaseUserService databaseUserService(UserRepository userRepository) {

        return new DatabaseUserService(userRepository);
    }
}
