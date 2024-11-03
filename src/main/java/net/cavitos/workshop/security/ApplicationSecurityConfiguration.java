package net.cavitos.workshop.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfiguration extends VaadinWebSecurity {
    
    @Value("${security.cors.origins}")
    private String[] origins;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/actuator/**")
                            .permitAll() // GET requests don't need auth
                        .anyRequest().authenticated()
                ).oauth2Login(withDefaults())
        ;

          http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private GrantedAuthoritiesMapper customAuthoritiesMapper() {
        return authorities -> {

            final var mappedAuthorities = new HashSet<GrantedAuthority>();

            authorities.forEach(authority -> {

                if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                    final var idToken = oidcUserAuthority.getIdToken();
                    final var roles = (Collection<String>) idToken.getClaim("net.cavitos.app.roles");

                    roles.stream()
                            .map(role -> "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .forEach(mappedAuthorities::add);
                }
            });

            return mappedAuthorities;
        };
    }

}
