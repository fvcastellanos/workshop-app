package net.cavitos.workshop.views.provider;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
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
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.InvoiceService;
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

@PageTitle("Facturas de Proveedores")
@RolesAllowed({ "ROLE_user" })
@Route(value = "provider-invoices", layout = MainLayout.class)
public class ProviderInvoiceView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInvoiceView.class);

    private final InvoiceService invoiceService;
    private final Clock systemClock;

    private final Grid<InvoiceEntity> grid;

    private TextField searchText;
    private Select<TypeOption> invoiceStatus;

    protected ProviderInvoiceView(final AuthenticationContext authenticationContext,
                                  final DatabaseUserService databaseUserService,
                                  final InvoiceService invoiceService,
                                  final Clock systemClock) {
        super(authenticationContext, databaseUserService);

        this.invoiceService = invoiceService;
        this.systemClock = systemClock;

        grid = buildGrid();

        add(
                buildSearchTitle("Búsqueda"),
                buildSearchBox(),
                grid,
                paginator
        );

        search();
    }

    @Override
    protected Page<InvoiceEntity> performSearch() {

        final var status = invoiceStatus.getValue();

        final var result = invoiceService.search(tenant, "P", status.getValue(), searchText.getValue(),
                pagination.getPage(), pagination.getSize());

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Factura", event -> {
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

        final var statuses = List.of(
                new TypeOption("Activa", "A"),
                new TypeOption("Cerrada", "C"),
                new TypeOption("Cancelada", "D")
        );

        searchText = ComponentFactory.buildTextSearchField("70%");
        invoiceStatus = ComponentFactory.buildTypeSelect("30%", "Estado", statuses, "A");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(searchText, invoiceStatus);

        return searchBody;
    }

    private Grid<InvoiceEntity> buildGrid() {
        final var grid = ComponentFactory.buildGrid(InvoiceEntity.class);

        grid.addColumn(new ComponentRenderer<>(invoiceEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", invoiceEntity.getNumber());
//                        modalView.openDialogForEdit(tenant, workOrderEntity);
                    });

                    final var viewImage = new Image("img/icons/view-grid-svgrepo-com.svg", "Detalle");
                    viewImage.setWidth("20px");
                    viewImage.setHeight("20px");
                    viewImage.getStyle().set("cursor", "pointer");
                    viewImage.addClickListener(event -> {
                        LOGGER.info("Details: {}", invoiceEntity.getNumber());
//                        UI.getCurrent().navigate("work-orders-details/%s".formatted(workOrderEntity.getId()));
                    });

                    layout.add(editImage, viewImage);

                    return layout;
                })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(invoiceEntity -> {

                    final var invoiceDate = LocalDate.ofInstant(invoiceEntity.getInvoiceDate(), systemClock.getZone());
                    return new Text(invoiceDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

                })).setHeader("Fecha")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("contactEntity.taxId")
                .setHeader("NIT")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("contactEntity.name")
                .setHeader("Contacto")
                .setSortable(true)
                .setResizable(true)
                .setWidth("30%");

        grid.addColumn(new ComponentRenderer<>(invoiceEntity -> {

                    final var status = switch (invoiceEntity.getStatus()) {
                        case "A" -> "Activa";
                        case "C" -> "Cerrada";
                        default -> "Cancelada";
                    };

                    return new Text(status);
                })).setHeader("Estado")
                .setSortable(true)
                .setWidth("10%");

        return grid;
    }
}
