package net.cavitos.workshop.views.inventory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.InventoryMovementService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

@PageTitle("Inventario Inicial")
@RolesAllowed({ "ROLE_user" })
@Route(value = "inventory/initial", layout = MainLayout.class)
public class InitialInventoryView extends CRUDLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(InitialInventoryView.class);

    private DatePicker initialDate;
    private DatePicker finalDate;

    private final ZonedDateTimeFactory zonedDateTimeFactory;
    private final InventoryMovementService inventoryMovementService;

    private final String initialInventoryCode;

    private final Grid<InventoryEntity> grid;

    protected InitialInventoryView(final AuthenticationContext authenticationContext,
                                   final DatabaseUserService databaseUserService,
                                   @Value("${initial.inventory.movement-type.code:MI-01}") final String initialInventoryCode,
                                   final ZonedDateTimeFactory zonedDateTimeFactory,
                                   final InventoryMovementService inventoryMovementService) {
        super(authenticationContext, databaseUserService);

        this.initialInventoryCode = initialInventoryCode;

        this.zonedDateTimeFactory = zonedDateTimeFactory;
        this.inventoryMovementService = inventoryMovementService;

        this.grid = buildGrid();

        add(
                buildSearchTitle("Búsqueda"),
                buildSearchBox(),
                grid
        );

        search();
    }

    @Override
    protected Page<InventoryEntity> performSearch() {

        final var iDate = nonNull(initialDate.getValue()) ? zonedDateTimeFactory.buildInstantFromLocalDate(initialDate.getValue())
                : zonedDateTimeFactory.buildInstantFrom("2001-01-01");

        final var fDAte = nonNull(finalDate.getValue()) ? zonedDateTimeFactory.buildInstantFromLocalDate(finalDate.getValue())
                : zonedDateTimeFactory.buildInstantFrom("2100-01-01");

        final var result = inventoryMovementService.search("%", initialInventoryCode, iDate, fDAte, this.tenant, 0, 1000);

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Orden", event -> {
//            modalView.openDialogForNew(tenant);
        });

        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch, btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(buildSearchBody(), searchFooter);

        return searchBox;
    }

    private HorizontalLayout buildSearchBody() {

        initialDate = new DatePicker("Fecha Inicial");
        initialDate.setWidth("50%");

        finalDate = new DatePicker("Fecha Final");
        finalDate.setWidth("50%");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(initialDate, finalDate);

        return searchBody;
    }

    private Grid<InventoryEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(InventoryEntity.class);

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setPadding(true);
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", inventoryEntity.getId());
//                        modalView.openDialogForEdit(tenant, invoiceDetail);
                    });

                    final var deleteImage = new Image("img/icons/trash-can-svgrepo-com.svg", "Eliminar");
                    deleteImage.setWidth("20px");
                    deleteImage.setHeight("20px");
                    deleteImage.getStyle().set("cursor", "pointer");
                    deleteImage.addClickListener(event -> {
                        LOGGER.info("Delete: {}", inventoryEntity.getId());
//                        deleteDialog.openDialog(tenant, invoiceDetail);

                    });

                    layout.add(editImage, deleteImage);

                    return layout;
                })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

            final var date = zonedDateTimeFactory.buildStringFromInstant(inventoryEntity.getOperationDate());

            return new VerticalLayout(
                    new Text(date)
            );

        })).setHeader("Fecha")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("quantity")
                .setHeader("Cantidad")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("productEntity.code")
                .setHeader("Código")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("productEntity.name")
                .setHeader("Producto")
                .setSortable(true)
                .setResizable(true)
                .setWidth("20%");

        grid.addColumn("unitPrice")
                .setHeader("P. Unitario")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

            final var total = inventoryEntity.getQuantity() * inventoryEntity.getUnitPrice();
            return new Text(String.valueOf(total));
        })).setHeader("Total")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        return grid;
    }
}
