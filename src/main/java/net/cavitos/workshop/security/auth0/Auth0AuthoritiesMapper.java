package net.cavitos.workshop.security.auth0;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.Collection;
import java.util.HashSet;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class Auth0AuthoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        final var mappedAuthorities = new HashSet<GrantedAuthority>();

        emptyIfNull(authorities).forEach(authority -> {

            if (authority instanceof OidcUserAuthority oidcUserAuthority) {

                final var roles = oidcUserAuthority.getUserInfo()
                        .getClaimAsStringList("net.cavitos.app.roles");

                roles.stream()
                        .map(role -> "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(mappedAuthorities::add);
            }
        });

        return mappedAuthorities;
    }
}
