package net.cavitos.workshop.security.service;

import com.vaadin.flow.spring.security.AuthenticationContext;
import net.cavitos.workshop.domain.exception.AuthenticationException;
import net.cavitos.workshop.model.repository.ApplicationUserRepository;
import net.cavitos.workshop.security.domain.UserProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

public class DefaultUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserService.class);

    private final ApplicationUserRepository applicationUserRepository;
    private final AuthenticationContext authenticationContext;


    public DefaultUserService(final ApplicationUserRepository applicationUserRepository,
                              final AuthenticationContext authenticationContext) {

        this.applicationUserRepository = applicationUserRepository;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public UserProfile getUserProfile(final String username) {

        final var userIdentities = StringUtils.split(username, '|');

        final var userHolder = applicationUserRepository.findByUserIdAndProvider(userIdentities[1], userIdentities[0]);

        if (userHolder.isEmpty()) {

            LOGGER.error("user={} not found", username);
             throw new AuthenticationException("User not found");
        }

        final var user = userHolder.get();
        final var tenant = user.getTenantEntity();

        if ((user.getActive() != 1) || (tenant.getActive() != 1)) {

            LOGGER.error("user or tenant is not active - user_active={}, tenant_active={}", user.getActive(), tenant.getActive());
            throw new AuthenticationException("User is not active");
        }

        LOGGER.info("Profile information loaded for user={}", username);
        return UserProfile.builder()
                .username(username)
                .userId(userIdentities[1])
                .provider(userIdentities[0])
                .tenant(tenant.getCode())
                .roles(getRoles())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> getRoles() {

        final var authenticatedUser = authenticationContext.getAuthenticatedUser(DefaultOidcUser.class)
                        .orElseThrow(() -> new AuthenticationException("User not authenticated"));

        final var attributes = authenticatedUser.getAttributes();

        final var roles = (List<String>) attributes.get("net.cavitos.app.roles");
        return emptyIfNull(roles);
    }
}
