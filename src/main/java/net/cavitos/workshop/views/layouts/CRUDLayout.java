package net.cavitos.workshop.views.layouts;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import net.cavitos.workshop.security.service.DatabaseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public abstract class CRUDLayout extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final transient AuthenticationContext authenticationContext;
    private final DatabaseUserService databaseUserService;

    protected final String tenant;

    protected static final int DEFAULT_PAGE = 0;
    protected static final int DEFAULT_SIZE = 1000;

    protected CRUDLayout(final AuthenticationContext authenticationContext,
                         final DatabaseUserService databaseUserService) {
        this.authenticationContext = authenticationContext;
        this.databaseUserService = databaseUserService;
        this.tenant = getUserTenant();
    }

    protected void showErrorNotification(String message) {
        final var notification = new Notification(message, 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

    protected DefaultOidcUser getUserDetails() {
        return authenticationContext.getAuthenticatedUser(DefaultOidcUser.class)
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }

    protected String getUserTenant() {

        final var userDetails = getUserDetails();
        final var principalName = userDetails.<String>getAttribute("sub");

        logger.info("Get tenant for user: {}", principalName);

        final var userProfile = databaseUserService.getUserProfile(principalName);
        return userProfile.getTenant();
    }
}
