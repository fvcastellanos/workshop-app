package net.cavitos.workshop.views.layouts;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Layout
public class MainLayout  extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {

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
                createNavigationItem("Inicio", "", "line-awesome/svg/home.svg"),
                createNavigationItemWithSubItems("Catálogos", null, "line-awesome/svg/home.svg",
                        createNavigationItem("Marcas de Vehiculos", "car-brands", "line-awesome/svg/home.svg"),
                        createNavigationItem("Contactos", "contacts", "line-awesome/svg/home.svg"),
                        createNavigationItem("Productos", "products", "line-awesome/svg/home.svg"),
                        createNavigationItem("Categorías de Productos", "product-categories", "line-awesome/svg/home.svg"),
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

    private Footer createFooter() {
        Footer layout = new Footer();

//        Optional<User> maybeUser = authenticatedUser.get();
//        if (maybeUser.isPresent()) {
//            User user = maybeUser.get();
//
//            Avatar avatar = new Avatar(user.getName());
//            StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
//            avatar.setImageResource(resource);
//            avatar.setThemeName("xsmall");
//            avatar.getElement().setAttribute("tabindex", "-1");
//
//            MenuBar userMenu = new MenuBar();
//            userMenu.setThemeName("tertiary-inline contrast");
//
//            MenuItem userName = userMenu.addItem("");
//            Div div = new Div();
//            div.add(avatar);
//            div.add(user.getName());
//            div.add(new Icon("lumo", "dropdown"));
//            div.getElement().getStyle().set("display", "flex");
//            div.getElement().getStyle().set("align-items", "center");
//            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
//            userName.add(div);
//            userName.getSubMenu().addItem("Sign out", e -> {
//                authenticatedUser.logout();
//            });
//
//            layout.add(userMenu);
//        } else {
//            Anchor loginLink = new Anchor("login", "Sign in");
//            layout.add(loginLink);
//        }

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
