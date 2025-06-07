package net.cavitos.workshop.views.layouts;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.views.component.Paginator;
import net.cavitos.workshop.views.model.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;


public abstract class CRUDLayout extends VerticalLayout {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final int DEFAULT_PAGE = 0;
    protected static final int DEFAULT_SIZE = 25;

    private final transient AuthenticationContext authenticationContext;
    private final DatabaseUserService databaseUserService;

    protected final String tenant;

    protected Pagination pagination;
    protected Paginator paginator;

    protected CRUDLayout(final AuthenticationContext authenticationContext,
                         final DatabaseUserService databaseUserService) {
        this.authenticationContext = authenticationContext;
        this.databaseUserService = databaseUserService;
        this.tenant = getUserTenant();
        this.pagination = buildDefaultPagination();
        this.paginator = new Paginator(this.pagination, pg -> search());
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

    protected Pagination buildDefaultPagination() {

        return Pagination.builder()
                .size(DEFAULT_SIZE)
                .page(DEFAULT_PAGE)
                .totalPages(0)
                .totalElements(0)
                .build();
    }

    protected abstract Page<?> performSearch();

    protected void search() {

        final var result = performSearch();

        pagination = Pagination.builder()
                .size(result.getSize())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getNumberOfElements())
                .build();

        paginator.setPagination(pagination);
    }
}
