package net.cavitos.workshop.views.layouts;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.cavitos.workshop.resource.ImageLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.io.ByteArrayInputStream;
import java.util.List;

@Layout
public class MainLayout  extends AppLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainLayout.class);

    private final transient AuthenticationContext authenticationContext;

    private H1 viewTitle;

    public MainLayout(final AuthenticationContext authenticationContext) {

        this.authenticationContext = authenticationContext;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        final var appName = new Span("WorkShop");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        final var header = new Header(appName);

        final var scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNavItem createNavigationItem(final String title, final String path, final String icon) {

        if (StringUtils.isBlank(icon)) {
            return new SideNavItem(title, path);
        }

        return new SideNavItem(title, path, new SvgIcon(icon));
    }

    private SideNavItem createNavigationItemWithSubItems(final String title,
                                                         final String path,
                                                         final String icon,
                                                         final SideNavItem ... items) {

        SideNavItem item = StringUtils.isBlank(icon) ? new SideNavItem(title, path)
                : new SideNavItem(title, path, new SvgIcon(icon));
        item.addItem(items);

        return item;
    }

    private List<SideNavItem> buildMenuEntries() {

        return List.of(
                createNavigationItem("Inicio", "", "img/icons/home-1-svgrepo-com.svg"),
                createNavigationItemWithSubItems("Catálogos", null, "img/icons/list-svgrepo-com.svg",
                        createNavigationItem("Marcas de Vehiculos", "car-brands", "img/icons/car-svgrepo-com.svg"),
                        createNavigationItem("Contactos", "contacts", "img/icons/contacts-svgrepo-com.svg"),
                        createNavigationItem("Productos", "products", "img/icons/products-svgrepo-com.svg"),
                        createNavigationItem("Categorías de Productos", "product-categories", "img/icons/category-solid-svgrepo-com.svg"),
                        createNavigationItem("Tipos de Movimientos", "inventory-movement-types", "line-awesome/svg/home.svg")
                ),
                createNavigationItem("Ordenes", "work-orders", "line-awesome/svg/home.svg"),
                createNavigationItemWithSubItems("Movimientos", null, "line-awesome/svg/home.svg",
                        createNavigationItem("Facturas Proveedores", "provider-invoices", "line-awesome/svg/home.svg")
                ),
                createNavigationItemWithSubItems("Inventario", null, "line-awesome/svg/home.svg",
                        createNavigationItem("Inicial", "inventory/initial", "line-awesome/svg/home.svg"),
                        createNavigationItem("Movimientos", "inventory-adjustments", "line-awesome/svg/home.svg"),
                        createNavigationItem("Existencias", "inventory-transfers", "line-awesome/svg/home.svg")
                )
        );
    }

    private SideNav createNavigation() {
        final var nav = new SideNav();

        buildMenuEntries()
                .forEach(nav::addItem);

        return nav;
    }

    private Avatar buildAvatar(final DefaultOidcUser user) {

        final var userInfo = user.getUserInfo();

        final var avatar = new Avatar(userInfo.getFullName());
        avatar.setThemeName("xsmall");
        avatar.getElement().setAttribute("tabindex", "-1");

        try {
            final var image = ImageLoader.loadImageFromUrl(userInfo.getPicture());
            final var resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(image));
            avatar.setImageResource(resource);

        } catch (Exception exception) {
            LOGGER.error("Error loading image from url", exception);
        }

        return avatar;
    }


    private Footer createFooter() {
        Footer layout = new Footer();


        final var authenticatedUserHolder = authenticationContext.getAuthenticatedUser(DefaultOidcUser.class);
        if (authenticatedUserHolder.isPresent()) {
            var user = authenticatedUserHolder.get();

            var userInfo = user.getUserInfo();

            final var avatar = buildAvatar(user);

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            final var userName = userMenu.addItem("");

            Div div = new Div();
            div.add(avatar);
            div.add(userInfo.getFullName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);

            userName.getSubMenu().addItem("Profile", event -> {

                LOGGER.info("Show profile of user={}", userInfo.getNickName());
                // show profile
            });

            userName.getSubMenu().addItem("Sign out", e -> {

                UI.getCurrent().getPage().setLocation("/logout");
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return MenuConfiguration.getPageHeader(getContent())
                .orElse("");
    }

}
