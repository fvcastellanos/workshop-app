package net.cavitos.workshop.views.contact;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.ContactService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildStatusSelect;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTextSearchField;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTypeSelect;

@PageTitle("Contactos")
@RolesAllowed({ "ROLE_user" })
@Route(value = "contacts", layout = MainLayout.class)
public class ContactView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactView.class);

    private final ContactService contactService;

    private final Grid<ContactEntity> grid;
    private TextField searchText;
    private Select<Status> searchStatus;
    private Select<TypeOption> searchType;

    private final AddContactModal addContactModal;

    public ContactView(final AuthenticationContext authenticationContext,
                       final DatabaseUserService userDatabaseService,
                       final ContactService contactService,
                       final AddContactModal addModelDialog) {

        super(authenticationContext, userDatabaseService);
        this.contactService = contactService;

        this.addContactModal = addModelDialog;
        this.grid = buildGrid();

        final var btnSearch = new Button("Buscar", event -> {

            performSearch();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Contacto", event -> {
            addModelDialog.openDialogForNew(getUserTenant());
        });

        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchBody = buildSearchBody();
        searchBody.add(searchText);
        searchBody.add(searchStatus);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch);
        searchFooter.add(btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(buildSearchBody());
        searchBox.add(searchFooter);

        add(buildSearchTitle("Búsqueda"));
        add(searchBox);
        add(grid);

        addModelDialog.addOnSaveEvent(entity -> performSearch());

        performSearch();
    }

    private VerticalLayout buildSearchBody() {

        searchText = buildTextSearchField("100%");

        searchStatus = buildStatusSelect("20%", StatusTransformer.toView(1));
        searchStatus.setWidth("50%");

        final var contactTypes = List.of(
                new TypeOption("Todos", "%"),
                new TypeOption("Cliente", "C"),
                new TypeOption("Proveedor", "P")
        );

        searchType = buildTypeSelect("50%", "Tipo de Contacto", contactTypes, "%");

        final var row1 = ComponentFactory.buildSearchBody();
        row1.add(searchText);

        final var row2 = ComponentFactory.buildSearchBody();
        row2.add(searchType);
        row2.add(searchStatus);

        return  new VerticalLayout(row1, row2);
    }

    @Override
    protected Page<ContactEntity> performSearch() {

        LOGGER.info("Search text: {} - Status: {} - Type: {}", searchText.getValue(), searchStatus.getValue(), searchType.getValue());

        final var tenant = getUserTenant();
        final var type = searchType.getValue();
        final var status = searchStatus.getValue();

        final var result = contactService.search(tenant, type.getValue(), status.getValue(), searchText.getValue(),
                DEFAULT_PAGE, DEFAULT_SIZE);

        grid.setItems(result.getContent());

        return result;
    }

    private Grid<ContactEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(ContactEntity.class);

        grid.addColumn(new ComponentRenderer<>(contactEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", contactEntity.getName());
                        addContactModal.openDialogForEdit(getUserTenant(), contactEntity);
                    });

                    layout.add(editImage);

                    return layout;
                })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn("code")
                .setHeader("Código")
                .setSortable(true)
                .setResizable(true)
                .setWidth("20%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("40%");

        grid.addColumn(new ComponentRenderer<>(contactEntity -> {

                    final var type = contactEntity.getType().equals("C") ? "Cliente" : "Proveedor";
                    return new Text(type);
                })).setHeader("Tipo")
                .setSortable(true)
                .setWidth("20%");

        grid.addColumn(new ComponentRenderer<>(contactEntity -> {

                    final var active = contactEntity.getActive() == 1 ? "Activo" : "Inactivo";
                    return new Text(active);
                })).setHeader("Activo")
                .setSortable(true);

        return grid;
    }
}
