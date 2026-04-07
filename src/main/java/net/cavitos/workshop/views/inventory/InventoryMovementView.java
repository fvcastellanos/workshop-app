package net.cavitos.workshop.views.inventory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.InventoryEntity;
import net.cavitos.workshop.security.service.DefaultUserService;
import net.cavitos.workshop.service.InventoryMovementService;
import net.cavitos.workshop.service.InventoryMovementTypeService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.TypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;

@PageTitle("Movimientos de Inventario")
@RolesAllowed({ "ROLE_admin" })
@Route(value = "inventory", layout = MainLayout.class)
public class InventoryMovementView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryMovementView.class);

    private final InventoryMovementService inventoryMovementService;
    private final InventoryMovementTypeService inventoryMovementTypeService;
    private final ZonedDateTimeFactory zonedDateTimeFactory;
    private final AuthenticationContext authenticationContext;

    private final Grid<InventoryEntity> grid;

    private DatePicker initialDate;
    private DatePicker finalDate;
    private Select<TypeOption> movementTypeField;
    private TextField searchText;

    public InventoryMovementView(final AuthenticationContext authenticationContext,
                                 final DefaultUserService defaultUserService,
                                 final InventoryMovementService inventoryMovementService,
                                 final InventoryMovementTypeService inventoryMovementTypeService,
                                 final ZonedDateTimeFactory zonedDateTimeFactory) {
        super(authenticationContext, defaultUserService);

        this.inventoryMovementService = inventoryMovementService;
        this.inventoryMovementTypeService = inventoryMovementTypeService;
        this.zonedDateTimeFactory = zonedDateTimeFactory;
        this.authenticationContext = authenticationContext;

        this.grid = buildGrid();

        add(
            ComponentFactory.buildSearchTitle("Búsqueda"),
            buildSearchBox(),
            grid,
            paginator
        );

        search();
    }

    @Override
    protected Page<InventoryEntity> performSearch() {

        final var iDate = nonNull(initialDate.getValue()) ? zonedDateTimeFactory.buildInstantFromLocalDate(initialDate.getValue())
                : zonedDateTimeFactory.buildInstantFrom("2001-01-01");

        final var fDAte = nonNull(finalDate.getValue()) ? zonedDateTimeFactory.buildInstantFromLocalDate(finalDate.getValue())
                : zonedDateTimeFactory.buildInstantFrom("2100-01-01");

        final var typeOption = movementTypeField.getValue();

        final var result = inventoryMovementService.search("%", typeOption.getValue(), iDate, fDAte, tenant,
                pagination.getPage(), pagination.getSize());

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox()  {


        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        authenticationContext.getAuthenticatedUser(DefaultOidcUser.class)
                .ifPresent(user -> {

                    user.getAuthorities().forEach(grantedAuthority -> {

                        LOGGER.info("Authority: {}", grantedAuthority.getAuthority());
                    });
                });

//        final var btnAdd = new Button("Agregar Movimiento", event -> {
//            modalView.openDialogForNew(tenant);
//        });

//        btnAdd.setWidth("min-content");
//        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(buildSearchBody(), searchFooter);

//        modalView.addOnSaveEvent(entity -> performSearch());
//        deleteDialog.addOnDeleteEvent(entity -> performSearch());


        return searchBox;
    }

    public HorizontalLayout buildSearchBody() {

        initialDate = ComponentFactory.buildDatePicker("Fecha Inicial", "30%");
        finalDate = ComponentFactory.buildDatePicker("Fecha Final", "30%");

        movementTypeField = ComponentFactory.buildTypeSelect("40%", "Tipo de Movimiento", getMovementTypes(), "%");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(
                initialDate,
                finalDate,
                movementTypeField
        );

        return searchBody;
    }

    private Grid<InventoryEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(InventoryEntity.class);

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

                    final var date = zonedDateTimeFactory.buildStringFromInstant(inventoryEntity.getOperationDate());

                    return new VerticalLayout(
                            new Text(date)
                    );

                })).setHeader("Fecha")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

            final var movementType = inventoryEntity.getInventoryMovementTypeEntity();
            return new VerticalLayout(new Text(movementType.getCode() + " - " + movementType.getName()));
        }))
                .setHeader("Tipo de Movimiento")
                .setSortable(true)
                .setResizable(true)
                .setWidth("15%");

        grid.addColumn("quantity")
                .setHeader("Cantidad")
                .setSortable(true)
                .setResizable(true)
                .setWidth("5%");

        grid.addColumn(new ComponentRenderer<>(inventoryEntity -> {

            final var product = inventoryEntity.getProductEntity();
            return new VerticalLayout(new Text(product.getProductCategoryEntity().getCode() + " - " + product.getProductCategoryEntity().getName()));
        }))
                .setHeader("Tipo de Movimiento")
                .setSortable(true)
                .setResizable(true)
                .setWidth("15%");

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

    private List<TypeOption> getMovementTypes() {

        final var movementTypes = inventoryMovementTypeService.findActive(tenant).stream()
                .map(type -> new TypeOption(type.getCode() + " - " + type.getName(), type.getCode()))
                .toList();

        final var options = new ArrayList<TypeOption>(movementTypes);
        options.addFirst(new TypeOption("Todos", "%"));

        return options;
    }
}
