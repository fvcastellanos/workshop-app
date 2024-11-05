package net.cavitos.workshop.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import net.cavitos.workshop.security.auth0.Auth0AuthoritiesMapper;
import net.cavitos.workshop.security.auth0.Auth0Configuration;
import net.cavitos.workshop.security.auth0.Auth0LogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Import(Auth0Configuration.class)
public class ApplicationSecurityConfiguration extends VaadinWebSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
                                                   final Auth0LogoutHandler applicationLogoutHandler,
                                                   final Auth0AuthoritiesMapper auth0AuthoritiesMapper) throws Exception {

        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/actuator/**")
                            .permitAll() // GET requests don't need auth
                        .anyRequest().authenticated()
                );
               
        http.oauth2Login(oAuth2LoginConfigurer -> {
            oAuth2LoginConfigurer.loginPage("/oauth2/authorization/okta");
            oAuth2LoginConfigurer.userInfoEndpoint(config -> config.userAuthoritiesMapper(auth0AuthoritiesMapper));
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.logout(logout -> logout.addLogoutHandler(applicationLogoutHandler)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
        );

        return http.build();
    }
}
