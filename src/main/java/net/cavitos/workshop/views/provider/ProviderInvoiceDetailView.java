package net.cavitos.workshop.views.provider;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.model.entity.InvoiceDetailEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.InvoiceDetailService;
import net.cavitos.workshop.service.InvoiceService;
import net.cavitos.workshop.transformer.InvoiceDetailTransformer;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;

@RolesAllowed({ "ROLE_user" })
@PageTitle("Detalle de Factura de Proveedor")
@Route(value = "provider-invoices-details", layout = MainLayout.class)
public class ProviderInvoiceDetailView extends CRUDLayout implements HasUrlParameter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInvoiceDetailView.class);

    private final InvoiceService invoiceService;
    private final InvoiceDetailService invoiceDetailService;

    private final Clock systemClock;

    private final H3 searchTitle;
    private final ProviderInvoiceDetailModalView modalView;
    private final ProviderInvoiceDeleteDialog deleteDialog;

    private TextField invoiceDate;
    private TextField status;
    private TextField taxId;
    private TextField contact;
    private TextField dueDate;
    private TextField total;

    private final Grid<InvoiceDetail> grid;

    private InvoiceEntity invoiceEntity;

    protected ProviderInvoiceDetailView(final AuthenticationContext authenticationContext,
                                        final DatabaseUserService databaseUserService,
                                        final InvoiceService invoiceService,
                                        final InvoiceDetailService invoiceDetailService,
                                        final Clock systemClock,
                                        final ProviderInvoiceDetailModalView modalView,
                                        final ProviderInvoiceDeleteDialog deleteDialog) {
        super(authenticationContext, databaseUserService);

        this.invoiceService = invoiceService;
        this.invoiceDetailService = invoiceDetailService;
        this.systemClock = systemClock;
        this.modalView = modalView;
        this.deleteDialog = deleteDialog;

        searchTitle = buildSearchTitle("");
        grid = buildGrid();

        add(
                searchTitle,
                buildInvoiceInformationBox(),
                grid
        );
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String invoiceId) {

        LOGGER.info("Details for invoice: {}", invoiceId);
        invoiceEntity = invoiceService.findById(tenant, invoiceId);

        fillInvoiceInformation(invoiceEntity);

        performSearch();
    }

    @Override
    protected Page<InvoiceDetailEntity> performSearch() {

        final var invoiceDetails = invoiceDetailService.getInvoiceDetails(invoiceEntity.getId(), tenant);

        final var details = ListUtils.emptyIfNull(invoiceDetails)
                .stream()
                .map(InvoiceDetailTransformer::toWeb)
                .toList();

        grid.setItems(details);

        calculateTotal(invoiceDetails);

        return new PageImpl<>(invoiceDetails, PageRequest.of(0, 1000), invoiceDetails.size());
    }

    private void calculateTotal(final List<InvoiceDetailEntity> details) {

        final var total = ListUtils.emptyIfNull(details)
                .stream()
                .mapToDouble(InvoiceDetailEntity::getTotal)
                .sum();

        this.total.setValue(String.valueOf(total));
    }

    private VerticalLayout buildInvoiceInformationBox() {

        final var btnAdd = new Button("Agregar Detalle", event -> {
            modalView.setInvoiceId(invoiceEntity.getId());
            modalView.openDialogForNew(tenant);
        });
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var footerBox = ComponentFactory.buildSearchFooter();
        footerBox.setWidth("100%");
        footerBox.add(
                ComponentFactory.buildRedirectButton("Regresar", "provider-invoices"),
                btnAdd
        );

        invoiceDate = new TextField("Fecha");
        invoiceDate.setWidth("33%");
        invoiceDate.setReadOnly(true);

        taxId = new TextField("NIT");
        taxId.setWidth("33%");
        taxId.setReadOnly(true);

        contact = new TextField("Contacto");
        contact.setWidth("34%");
        contact.setReadOnly(true);

        status = new TextField("Estado");
        status.setWidth("33%");
        status.setReadOnly(true);

        dueDate = new TextField("Vencimiento");
        dueDate.setWidth("33%");
        dueDate.setReadOnly(true);

        total = new TextField("Total");
        total.setWidth("34%");
        total.setReadOnly(true);

        final var row1 = new HorizontalLayout(
                invoiceDate,
                taxId,
                contact
        );
        row1.setWidth("100%");

        final var row2 = new HorizontalLayout(
                status,
                dueDate,
                total
        );
        row2.setWidth("100%");

        final var detailBody = new VerticalLayout(
                row1,
                row2
        );
        detailBody.setWidth("100%");

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(
                detailBody,
                footerBox
        );

        modalView.addOnSaveEvent(entity -> performSearch());
        deleteDialog.addOnDeleteEvent(entity -> performSearch());

        return searchBox;
    }

    private Grid<InvoiceDetail> buildGrid() {

        final var grid = ComponentFactory.buildGrid(InvoiceDetail.class);

        grid.addColumn(new ComponentRenderer<>(invoiceDetail -> {

                    final var layout = new HorizontalLayout();
                    layout.setPadding(true);
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", invoiceDetail.getId());
                        modalView.openDialogForEdit(tenant, invoiceDetail);
                    });

                    final var deleteImage = new Image("img/icons/trash-can-svgrepo-com.svg", "Eliminar");
                    deleteImage.setWidth("20px");
                    deleteImage.setHeight("20px");
                    deleteImage.getStyle().set("cursor", "pointer");
                    deleteImage.addClickListener(event -> {
                        LOGGER.info("Delete: {}", invoiceDetail.getId());
                        deleteDialog.openDialog(tenant, invoiceDetail);

                    });

                    layout.add(editImage, deleteImage);

                    return layout;
                })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn("quantity")
                .setHeader("Cant")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("product.code")
                .setHeader("Código")
                .setSortable(true)
                .setResizable(true)
                .setWidth("15%");

        grid.addColumn("product.name")
                .setHeader("Producto")
                .setSortable(true)
                .setResizable(true)
                .setWidth("15%");

        grid.addColumn("unitPrice")
                .setHeader("P Uni")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("discountPercentage")
                .setHeader("Desc %")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("discountAmount")
                .setHeader("Desc")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("total")
                .setHeader("Total")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("workOrderNumber")
                .setHeader("Orden")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        return grid;
    }

    private void fillInvoiceInformation(final InvoiceEntity invoiceEntity) {

        searchTitle.setText("No. Factura: %s".formatted(invoiceEntity.getNumber()));

        final var date = LocalDate.ofInstant(invoiceEntity.getInvoiceDate(), systemClock.getZone())
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

//        final var due = LocalDate.ofInstant(invoiceEntity.getEffectiveDate(), systemClock.getZone())
//                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        final var contactEntity = invoiceEntity.getContactEntity();

        final var invoiceStatus = switch (invoiceEntity.getStatus()) {
            case "A" -> "Activa";
            case "D" -> "Anulada";
            default -> "Cancelada";
        };

        invoiceDate.setValue(date);
        taxId.setValue(contactEntity.getTaxId());
        contact.setValue(contactEntity.getName());

        status.setValue(invoiceStatus);
//        dueDate.setValue(due);


    }
}
