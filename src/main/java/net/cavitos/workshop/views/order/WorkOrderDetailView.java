package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.service.WorkOrderService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

@PageTitle("Detalle de Orden de Trabajo")
@RolesAllowed({ "ROLE_user" })
@Route(value = "work-orders-details", layout = MainLayout.class)
public class WorkOrderDetailView extends CRUDLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailView.class);

    private final Clock systemClock;
    private final WorkOrderService workOrderService;
    private final WorkOrderDetailService workOrderDetailService;

    private final Grid<WorkOrderDetailEntity> grid;

    private WorkOrderEntity workOrderEntity;

    private H3 searchTitle;
    private TextField orderDate;
    private TextField plateNumber;
    private TextField contact;
    private TextField carModel;
    private TextField status;
    private TextArea notes;

    protected WorkOrderDetailView(final AuthenticationContext authenticationContext,
                                  final DatabaseUserService databaseUserService,
                                  final WorkOrderService workOrderService,
                                  final WorkOrderDetailService workOrderDetailService,
                                  final Clock systemClock) {
        super(authenticationContext, databaseUserService);

        this.systemClock = systemClock;
        this.workOrderService = workOrderService;
        this.workOrderDetailService = workOrderDetailService;

        searchTitle = buildSearchTitle("Búsqueda");

        grid = buildGrid();

        add(
                searchTitle,
                buildOrderInformationBox(),
                grid
        );
    }

    @Override
    public void setParameter(final BeforeEvent beforeEvent, final String workOrderId) {

        LOGGER.info("Details for Work Order Id: {}", workOrderId);
        workOrderEntity = workOrderService.findById(tenant, workOrderId);

        fillOrderInformation(workOrderEntity);

        performSearch();
    }

    @Override
    protected Page<WorkOrderDetailEntity> performSearch() {

        final var details = workOrderDetailService.getOrderDetails(tenant, workOrderEntity.getId());

        grid.setItems(details);

        return new PageImpl<WorkOrderDetailEntity>(details, Pageable.unpaged(), details.size());
    }

    private VerticalLayout buildOrderInformationBox() {

        final var footerBox = ComponentFactory.buildSearchFooter();
        footerBox.setWidth("100%");
        footerBox.add(
                ComponentFactory.buildRedirectButton("Regresar", "work-orders")
        );

        orderDate = new TextField("Fecha");
        orderDate.setReadOnly(true);
        orderDate.setWidth("20%");

        plateNumber = new TextField("No. Placa");
        plateNumber.setReadOnly(true);
        orderDate.setWidth("30%");

        contact = new TextField("Contacto");
        contact.setReadOnly(true);
        contact.setWidth("50%");

        carModel = new TextField("Modelo");
        carModel.setReadOnly(true);
        carModel.setWidth("30%");

        status = new TextField("Estado");
        status.setReadOnly(true);
        status.setWidth("30%");

        notes = new TextArea("Notas");
        notes.setReadOnly(true);
        notes.setWidth("100%");

        final var row1 = new HorizontalLayout(
                orderDate,
                plateNumber,
                contact
        );
        row1.setWidth("100%");

        final var row2 = new HorizontalLayout(
                carModel,
                status
        );
        row2.setWidth("100%");

        final var row3 = new HorizontalLayout(
                notes
        );
        row3.setWidth("100%");

        final var detailBody = new VerticalLayout();
        detailBody.setWidth("100%");
        detailBody.add(
                row1,
                row2,
                row3
        );

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(
                detailBody,
                footerBox
        );

        return searchBox;
    }

    private void fillOrderInformation(final WorkOrderEntity workOrderEntity) {

        searchTitle.setText("Orden No. %s".formatted(workOrderEntity.getNumber()));

        final var localDate = LocalDate.ofInstant(workOrderEntity.getOrderDate(), systemClock.getZone())
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        orderDate.setValue(localDate);
        plateNumber.setValue(workOrderEntity.getPlateNumber());
        contact.setValue(workOrderEntity.getContactEntity().getName());

        carModel.setValue(workOrderEntity.getCarLineEntity().getName());

        final var statusText = switch (workOrderEntity.getStatus()) {

            case "P" -> "En Proceso";
            case "A" -> "Cancelada";
            case "C" -> "Cerrada";
            default -> "Entregada";
        };

        status.setValue(statusText);

        notes.setValue(workOrderEntity.getNotes());

    }

    private Grid<WorkOrderDetailEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(WorkOrderDetailEntity.class);

        grid.addColumn(new ComponentRenderer<>(workOrderDetailEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var deleteImage = new Image("img/icons/trash-can-svgrepo-com.svg", "Eliminar");
                    deleteImage.setWidth("20px");
                    deleteImage.setHeight("20px");
                    deleteImage.getStyle().set("cursor", "pointer");
                    deleteImage.addClickListener(event -> {
                        LOGGER.info("Delete: {}", workOrderDetailEntity.getId());
//                        modalView.openDialogForEdit(tenant, workOrderEntity);
                    });

                    layout.add(deleteImage);

                    return layout;
        })).setHeader("#")
          .setSortable(false)
          .setResizable(false)
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
            .setWidth("30%");

        grid.addColumn("unitPrice")
            .setHeader("Precio Unitario")
            .setSortable(true)
            .setWidth("10%");

        return grid;
    }
}
