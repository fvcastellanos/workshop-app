package net.cavitos.workshop.views.movement;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
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
import net.cavitos.workshop.model.entity.InventoryMovementTypeEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.InventoryMovementTypeService;
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

@PageTitle("Tipos de Movimiento")
@RolesAllowed({ "ROLE_user" })
@Route(value = "inventory-movement-types", layout = MainLayout.class)
public class MovementTypeView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovementTypeView.class);

    private final InventoryMovementTypeService movementTypeService;

    private final Grid<InventoryMovementTypeEntity> grid;

    private final MovementTypeModalView modalView;

    private Select<TypeOption> movementType;
    private Select<Status> statusType;
    private TextField searchText;

    public MovementTypeView(final AuthenticationContext authenticationContext,
                            final DatabaseUserService databaseUserService,
                            final InventoryMovementTypeService movementTypeService,
                            final MovementTypeModalView modalView) {

        super(authenticationContext, databaseUserService);

        this.movementTypeService = movementTypeService;

        this.modalView = modalView;

        this.grid = buildGrid();

        add(
            buildSearchTitle("Búsqueda"),
            buildSearchBox(),
            grid,
            paginator
        );

        modalView.addOnSaveEvent(entity -> search());

        search();
    }

    @Override
    protected Page<InventoryMovementTypeEntity> performSearch() {

        final var status = statusType.getValue();
        final var type = movementType.getValue();

        final var result = movementTypeService.search(
                status.getValue(),
                type.getValue(),
                searchText.getValue(),
                tenant,
                pagination.getPage(),
                pagination.getSize()
        );

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Movimiento", event -> {
            modalView.openDialogForNew(tenant);
        });

        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch, btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(
                buildSearchBody(),
                searchFooter
        );

        return searchBox;
    }

    private VerticalLayout buildSearchBody() {

        final var movementTypes = List.of(
                new TypeOption("Todos", "%"),
                new TypeOption("Entrada", "I"),
                new TypeOption("Salida", "O")
        );

        searchText = buildTextSearchField("100%");
        movementType = buildTypeSelect("50%", "Tipo de Movimiento", movementTypes, "%");
        statusType = buildStatusSelect("50%", StatusTransformer.toView(1));

        final var row1 = ComponentFactory.buildSearchBody();
        row1.add(searchText);

        final var row2 = ComponentFactory.buildSearchBody();
        row2.add(movementType);
        row2.add(statusType);

        return new VerticalLayout(
                row1,
                row2
        );
    }

    private Grid<InventoryMovementTypeEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(InventoryMovementTypeEntity.class);

        grid.addColumn(new ComponentRenderer<>(movementTypeEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", movementTypeEntity.getName());
                        modalView.openDialogForEdit(tenant, movementTypeEntity);
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
                .setWidth("10%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("30%");

        grid.addColumn(new ComponentRenderer<>(movementTypeEntity -> {

                    final var type = movementTypeEntity.getType().equals("I") ? "Entrada" : "Salida";
                    return new Text(type);
                })).setHeader("Tipo")
                .setSortable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(movementTypeEntity -> {

                    final var active = movementTypeEntity.getActive() == 1 ? "Activo" : "Inactivo";
                    return new Text(active);
                })).setHeader("Activo")
                .setSortable(true);

        return grid;
    }
}
