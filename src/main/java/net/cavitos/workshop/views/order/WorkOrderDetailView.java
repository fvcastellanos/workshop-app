package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.security.service.DefaultUserService;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.service.WorkOrderService;
import net.cavitos.workshop.transformer.WorkOrderDetailTransformer;
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
import java.util.Objects;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

@PageTitle("Detalle de Orden de Trabajo")
@RolesAllowed({ "ROLE_user" })
@Route(value = "work-orders-details", layout = MainLayout.class)
public class WorkOrderDetailView extends CRUDLayout implements HasUrlParameter<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(WorkOrderDetailView.class);

    private final Clock systemClock;
    private final WorkOrderService workOrderService;
    private final WorkOrderDetailService workOrderDetailService;
    private final WorkOrderDetailModalView modalView;
    private final WorkOrderLaborModalView laborModalView;
    private final OrderDetailDeleteDialog deleteDialog;

    private final Grid<WorkOrderDetailEntity> grid;
    private final Grid<WorkOrderDetailEntity> laborGrid;

    private WorkOrderEntity workOrderEntity;

    private final H3 searchTitle;

    private TextField orderDate;
    private TextField plateNumber;
    private TextField contact;
    private TextField carModel;
    private TextField status;
    private TextArea notes;
    private TextField workOrderTotal;

    protected WorkOrderDetailView(final AuthenticationContext authenticationContext,
                                  final DefaultUserService defaultUserService,
                                  final WorkOrderService workOrderService,
                                  final WorkOrderDetailService workOrderDetailService,
                                  final WorkOrderDetailModalView modalView,
                                  final WorkOrderLaborModalView laborModalView,
                                  final OrderDetailDeleteDialog deleteDialog,
                                  final Clock systemClock) {
        super(authenticationContext, defaultUserService);

        this.systemClock = systemClock;
        this.workOrderService = workOrderService;
        this.workOrderDetailService = workOrderDetailService;
        this.modalView = modalView;
        this.laborModalView = laborModalView;
        this.deleteDialog = deleteDialog;

        searchTitle = buildSearchTitle("Búsqueda");

        grid = buildGrid();
        laborGrid =buildLaborGrid();

        add(
                searchTitle,
                buildOrderInformationBox(),
                buildTabSheet()
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

        final var products = details.stream()
                .filter(detail -> Objects.nonNull(detail.getProductEntity()))
                .toList();

        final var labor = details.stream()
                .filter(detail -> Objects.isNull(detail.getProductEntity()))
                .toList();

        grid.setItems(products);
        laborGrid.setItems(labor);

        return new PageImpl<>(details, Pageable.unpaged(), details.size());
    }

    private void calculateTotal() {

        final var details = workOrderDetailService.getOrderDetails(tenant, workOrderEntity.getId());
        final var total = details.stream()
                .mapToDouble(WorkOrderDetailEntity::getSalePrice)
                .sum();

        workOrderTotal.setValue(String.valueOf(total));
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

        workOrderTotal = new TextField("Total");
        workOrderTotal.setReadOnly(true);
        workOrderTotal.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        workOrderTotal.setWidth("30%");

        final var row1 = new HorizontalLayout(
                orderDate,
                plateNumber,
                contact
        );
        row1.setWidth("100%");

        final var row2 = new HorizontalLayout(
                carModel,
                status,
                workOrderTotal
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

        modalView.addOnSaveEvent(entity ->  {
            updateValues();
        });

        laborModalView.addOnSaveEvent(entity ->  {
            updateValues();
        });

        deleteDialog.addOnDeleteEvent(entity ->  {
            updateValues();
        });

        return searchBox;
    }

    private void updateValues() {
        performSearch();
        calculateTotal();
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

        calculateTotal();
    }

    private TabSheet buildTabSheet() {

        final var btnAdd = new Button("Agregar Detalle", event -> {

            modalView.setWorkOrderId(workOrderEntity.getId());
            modalView.openDialogForNew(tenant);
        });
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var btnAddLabor = new Button("Agregar Mano de Obra", event -> {

            laborModalView.setWorkOrderId(workOrderEntity.getId());
            laborModalView.openDialogForNew(tenant);
        });
        btnAddLabor.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var productsTab = new VerticalLayout(
                btnAdd,
                grid
        );

        final var laborTab = new VerticalLayout(
                btnAddLabor,
                laborGrid
        );

        final var tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.add("Productos", productsTab);
        tabSheet.add("Mano de Obra", laborTab);

        return tabSheet;
    }

    private Grid<WorkOrderDetailEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(WorkOrderDetailEntity.class);

        grid.addColumn(new ComponentRenderer<>(workOrderDetailEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", workOrderDetailEntity.getId());
                        modalView.openDialogForEdit(tenant, WorkOrderDetailTransformer.toWeb(workOrderDetailEntity));
                    });

                    final var deleteImage = new Image("img/icons/trash-can-svgrepo-com.svg", "Eliminar");
                    deleteImage.setWidth("20px");
                    deleteImage.setHeight("20px");
                    deleteImage.getStyle().set("cursor", "pointer");
                    deleteImage.addClickListener(event -> {
                        LOGGER.info("Delete: {}", workOrderDetailEntity.getId());
                        deleteDialog.openDialog(tenant, workOrderDetailEntity);
                    });

                    layout.add(editImage, deleteImage);

                    return layout;
        })).setHeader("#")
          .setSortable(false)
          .setResizable(false)
          .setWidth("5%");

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
            .setHeader("Costo")
            .setSortable(true)
            .setWidth("10%");

        grid.addColumn("salePrice")
            .setHeader("Venta")
            .setSortable(true)
            .setWidth("10%");

        return grid;
    }

    private Grid<WorkOrderDetailEntity> buildLaborGrid() {

        final var grid = ComponentFactory.buildGrid(WorkOrderDetailEntity.class);

        grid.addColumn(new ComponentRenderer<>(workOrderDetailEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", workOrderDetailEntity.getId());
                        laborModalView.openDialogForEdit(tenant, WorkOrderDetailTransformer.toWeb(workOrderDetailEntity));
                    });

                    final var deleteImage = new Image("img/icons/trash-can-svgrepo-com.svg", "Eliminar");
                    deleteImage.setWidth("20px");
                    deleteImage.setHeight("20px");
                    deleteImage.getStyle().set("cursor", "pointer");
                    deleteImage.addClickListener(event -> {
                        LOGGER.info("Delete: {}", workOrderDetailEntity.getId());
                        deleteDialog.openDialog(tenant, workOrderDetailEntity);
                    });

                    layout.add(editImage, deleteImage);

                    return layout;
        })).setHeader("#")
            .setSortable(false)
            .setResizable(false)
            .setWidth("1%");

        grid.addColumn("description")
            .setHeader("Descripción")
            .setSortable(true)
            .setResizable(true)
            .setWidth("30%");

        grid.addColumn("salePrice")
            .setHeader("Precio")
            .setSortable(true)
            .setResizable(true)
            .setWidth("10%");

        return grid;
    }
}
