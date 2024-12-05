package net.cavitos.workshop.views.order;

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
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.WorkOrderService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.TypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

@PageTitle("Ordenes de Trabajo")
@RolesAllowed({ "ROLE_user" })
@Route(value = "work-orders", layout = MainLayout.class)
public class WorkOrderView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderView.class);

    private final Clock systemClock;
    private final WorkOrderService workOrderService;
    private final Grid<WorkOrderEntity> grid;
    private final WorkOrderModalView modalView;

    private Select<TypeOption> orderStatus;
    private TextField searchText;

    protected WorkOrderView(final AuthenticationContext authenticationContext,
                            final DatabaseUserService databaseUserService,
                            final WorkOrderService workOrderService,
                            final Clock systemClock,
                            final WorkOrderModalView modalView) {

        super(authenticationContext, databaseUserService);

        this.systemClock = systemClock;
        this.workOrderService = workOrderService;
        this.modalView = modalView;

        grid = buildGrid();

        add(
                buildSearchTitle("Búsqueda"),
                buildSearchBox(),
                grid,
                paginator
        );

        this.modalView.addOnSaveEvent(entity -> search());

        search();
    }

    @Override
    protected Page<WorkOrderEntity> performSearch() {

        final var text = searchText.getValue();
        final var status = orderStatus.getValue();

        LOGGER.info("Searching for work orders with text: {} and status: {}", text, status);

        final var result = workOrderService.search(tenant, text, status.getValue(), pagination.getPage(), pagination.getSize());

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Orden", event -> {
            modalView.openDialogForNew(tenant);
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

        final var orderStatuses = List.of(
                new TypeOption("Todas", "%"),
                new TypeOption("En Proceso", "P"),
                new TypeOption("Cancelada", "A"),
                new TypeOption("Cerrada", "C"),
                new TypeOption("Entregada", "D")
        );

        searchText = ComponentFactory.buildTextSearchField("70%");
        orderStatus = ComponentFactory.buildTypeSelect("30%", "Estado", orderStatuses, "%");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(searchText, orderStatus);

        return searchBody;
    }

    private Grid<WorkOrderEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(WorkOrderEntity.class);

        grid.addColumn(new ComponentRenderer<>(workOrderEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", workOrderEntity.getNumber());
                        modalView.openDialogForEdit(tenant, workOrderEntity);
                    });

                    layout.add(editImage);

                    return layout;
                })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn("number")
                .setHeader("Número")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(workOrderEntity -> {

            final var orderDate = LocalDate.ofInstant(workOrderEntity.getOrderDate(), systemClock.getZone());
            final var text = orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            return new Text(text);

        })).setHeader("Fecha")
            .setSortable(true)
            .setResizable(true)
            .setWidth("10%");

        grid.addColumn("plateNumber")
                .setHeader("No. Placa")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("contactEntity.name")
                .setHeader("Contacto")
                .setSortable(true)
                .setResizable(true)
                .setWidth("30%");

        grid.addColumn(new ComponentRenderer<>(workOrderEntity -> {

                    final var status = switch (workOrderEntity.getStatus()) {
                        case "P" -> "En Proceso";
                        case "A" -> "Cancelada";
                        case "C" -> "Cerrada";
                        default -> "Entregada";
                    };

                    return new Text(status);
                })).setHeader("Estado")
                .setSortable(true)
                .setWidth("10%");

        return grid;
    }
}
