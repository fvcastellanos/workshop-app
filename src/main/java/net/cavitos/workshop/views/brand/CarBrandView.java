package net.cavitos.workshop.views.brand;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.CarBrandService;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PageTitle("Marcas de Vehículos")
@RolesAllowed({ "ROLE_user" })
@Route(value = "car-brands", layout = MainLayout.class)
public class CarBrandView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarBrandView.class);

    private final TextField searchText;
    private final Select<Status> searchStatus;
    private final Grid<CarBrandEntity> grid;

    private final CarBrandService carBrandService;
    private final AddModal addModal;

    public CarBrandView(final AuthenticationContext authenticationContext,
                        final DatabaseUserService databaseUserService,
                        final CarBrandService carBrandService,
                        final AddModal addModal) {

        super(authenticationContext, databaseUserService);
        this.carBrandService = carBrandService;
        this.addModal = addModal;

        final var h3 = new H3();
        h3.setText("Búsqueda");
        h3.setWidth("max-content");

        this.grid = buildGrid();

        this.searchText = buildTextSearchField("80%");
        this.searchStatus = buildStatusSelect("20%");

        final var searchBody = new HorizontalLayout();
        searchBody.setWidth("100%");
        searchBody.add(searchText);
        searchBody.add(searchStatus);

        final var searchBox = new VerticalLayout();
        searchBox.setClassName("border");
        searchBox.setWidthFull();
        searchBox.setWidth("100%");
        searchBox.getStyle().set("flex-grow", "1");
        searchBody.setHeightFull();
        searchBody.addClassName(LumoUtility.Gap.MEDIUM);
        searchBody.getStyle().set("flex-grow", "1");

        final var searchFooter = new HorizontalLayout();
        searchFooter.setWidthFull();
        searchFooter.addClassName(LumoUtility.Gap.MEDIUM);
        searchFooter.setWidth("100%");
        searchFooter.getStyle().set("flex-grow", "1");

        final var btnSearch = new Button();
        final var btnAdd = new Button();
        btnSearch.setText("Buscar");
        btnSearch.setWidth("min-content");
        btnAdd.setText("Agregar Vehículo");
        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        btnSearch.addClickListener(event -> {
            LOGGER.info("Search text: {} - Status: {}", searchText.getValue(), searchStatus.getValue());
            performSearch();
        });

        btnAdd.addClickListener(event -> addModal.openDialogForNew(tenant));

        searchFooter.add(btnSearch);
        searchFooter.add(btnAdd);

        setWidth("100%");
        getStyle().set("flex-grow", "1");
        setFlexGrow(1.0, searchBox);
        setFlexGrow(1.0, searchFooter);

        searchBox.add(searchBody, searchFooter);

        add(h3);
        add(searchBox);
        add(grid);

        addModal.addOnSaveEvent(entity -> performSearch());
        performSearch();
    }

    protected Select<Status> buildStatusSelect(final String width) {

        final var active = new Status(1, "Activo");
        final var inactive = new Status(0, "Inactivo");

        final var select = new Select<Status>();
        select.setLabel("Activo");
        select.setWidth(width);
        select.setItemLabelGenerator(Status::getLabel);
        select.setItems(active, inactive);
        select.setValue(active);

        return select;
    }

    protected TextField buildTextSearchField(final String width) {

        final var textField = new TextField();
        textField.setLabel("Texto");
        textField.setWidth(width);
        textField.setAutofocus(true);

        return textField;
    }

    protected Grid<CarBrandEntity> buildGrid() {

        final var grid = new Grid<>(CarBrandEntity.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");

        grid.addColumn(new ComponentRenderer<>(carBrandEntity -> {
            final var layout = new HorizontalLayout();
            layout.setWidthFull();
            layout.setJustifyContentMode(JustifyContentMode.CENTER);

            final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
            editImage.setWidth("20px");
            editImage.setHeight("20px");
            editImage.getStyle().set("cursor", "pointer");
            editImage.addClickListener(event -> {
                LOGGER.info("Edit: {}", carBrandEntity.getName());
                addModal.openDialogForEdit(tenant, carBrandEntity);
            });

            final var viewImage = new Image("img/icons/view-grid-svgrepo-com.svg", "Editar");
            viewImage.setWidth("20px");
            viewImage.setHeight("20px");
            viewImage.getStyle().set("cursor", "pointer");
            viewImage.addClickListener(event -> {
                LOGGER.info("Models for Brand: {}", carBrandEntity.getName());
                UI.getCurrent().navigate("car-models/%s".formatted(carBrandEntity.getId()));
            });

            layout.add(editImage, viewImage);

            return layout;
        })).setHeader("#")
                .setSortable(false)
                .setResizable(false)
                .setWidth("10%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("70%");

        grid.addColumn(new ComponentRenderer<>(carBrandEntity -> {

            final var active = carBrandEntity.getActive() == 1 ? "Activo" : "Inactivo";
            return new Text(active);
        })).setHeader("Activo")
            .setSortable(true);

        return grid;
    }

    private void performSearch() {

        final var status = searchStatus.getValue();
        final var result = carBrandService.getAllByTenant(tenant, status.getValue(), searchText.getValue(),
                DEFAULT_PAGE, DEFAULT_SIZE);

        grid.setItems(result.getContent());
    }
}
