package net.cavitos.workshop.views.brand;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.CarBrandService;
import net.cavitos.workshop.service.CarLineService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchBody;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildStatusSelect;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTextSearchField;

@RolesAllowed({ "ROLE_user" })
@PageTitle("Modelos de Vehículos")
@Route(value = "car-models", layout = MainLayout.class)
public class CarModelView extends CRUDLayout implements HasUrlParameter<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarModelView.class);

    private final H3 searchTitle;
    private final TextField searchText;
    private final Select<Status> searchStatus;
    private final Grid<CarLineEntity> grid;

    private final CarBrandService carBrandService;
    private final CarLineService carLineService;
    private final AddModelDialog addModelDialog;

    private CarBrandEntity carBrandEntity;

    public CarModelView(final AuthenticationContext authenticationContext,
                        final DatabaseUserService userDatabaseService,
                        final CarBrandService carBrandService,
                        final CarLineService carLineService,
                        final AddModelDialog addModelDialog) {

        super(authenticationContext, userDatabaseService);
        this.carBrandService = carBrandService;
        this.carLineService = carLineService;
        this.addModelDialog = addModelDialog;

        searchTitle = buildSearchTitle("Búsqueda");

        this.searchText = buildTextSearchField("80%");
        this.searchStatus = buildStatusSelect("20%", StatusTransformer.toView(1));

        final var btnBack = new Button("Regresar", buttonClickEvent -> {

            UI.getCurrent().navigate("car-brands");
        });
        btnBack.setWidth("min-content");
        btnBack.setIcon(new SvgIcon("img/icons/left-arrow-svgrepo-com.svg"));

        final var btnSearch = new Button("Buscar", event -> {

            performSearch();
        });
        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Modelo", event -> {

            addModelDialog.openDialogForNew(tenant, carBrandEntity);
        });
        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchBody = buildSearchBody();
        searchBody.add(searchText);
        searchBody.add(searchStatus);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnBack);
        searchFooter.add(btnSearch);
        searchFooter.add(btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(searchBody);
        searchBox.add(searchFooter);

        this.grid = buildGrid();

        addModelDialog.addOnSaveEvent(entity -> {

            performSearch();
        });

        add(searchTitle);
        add(searchBox);
        add(grid);
    }

    @Override
    public void setParameter(final BeforeEvent beforeEvent, final String carBrandId) {

        LOGGER.info("CarBrandID: {}", carBrandId);
        carBrandEntity = carBrandService.getById("resta", carBrandId);
        searchTitle.setText(carBrandEntity.getName());
        performSearch();
    }

    private void performSearch() {

        final var status = searchStatus.getValue();
        final var result = carLineService.findAll(tenant, carBrandEntity.getId(), status.getValue(),
                searchText.getValue(), DEFAULT_PAGE, DEFAULT_SIZE);

        grid.setItems(result.getContent());
    }

    protected Grid<CarLineEntity> buildGrid() {

        final var grid = new Grid<>(CarLineEntity.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");

        grid.addColumn(new ComponentRenderer<>(carLineEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", carLineEntity.getName());
                        addModelDialog.openDialogForEdit(tenant, carLineEntity);
                    });

                    layout.add(editImage);

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
}
