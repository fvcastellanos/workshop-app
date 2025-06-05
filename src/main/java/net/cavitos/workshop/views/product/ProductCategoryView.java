package net.cavitos.workshop.views.product;

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
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchFooter;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildSearchTitle;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildStatusSelect;
import static net.cavitos.workshop.views.factory.ComponentFactory.buildTextSearchField;

@PageTitle("Categorías de Productos")
@RolesAllowed({ "ROLE_user" })
@Route(value = "product-categories", layout = MainLayout.class)
public class ProductCategoryView extends CRUDLayout {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductCategoryView.class);

    private final ProductCategoryService productCategoryService;

    private final Grid<ProductCategoryEntity> grid;

    private final ProductCategoryModalView modalView;

    private TextField searchText;
    private Select<Status> searchStatus;

    public ProductCategoryView(final AuthenticationContext authenticationContext,
                               final DatabaseUserService databaseUserService,
                               final ProductCategoryService productCategoryService,
                               final ProductCategoryModalView modalView) {

        super(authenticationContext, databaseUserService);

        this.productCategoryService = productCategoryService;
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

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {
            search();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Categoría", event -> {

            modalView.openDialogForNew(tenant);
        });

        btnAdd.setWidth("min-content");
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        final var searchFooter = buildSearchFooter();
        searchFooter.add(btnSearch);
        searchFooter.add(btnAdd);

        final var searchBox = ComponentFactory.buildSearchBox();
        searchBox.add(buildSearchBody());
        searchBox.add(searchFooter);

        return searchBox;
    }

    private HorizontalLayout buildSearchBody() {

        searchText = buildTextSearchField("70%");
        searchStatus = buildStatusSelect("30%", StatusTransformer.toView(1));

        final var row = ComponentFactory.buildSearchBody();
        row.add(searchText);
        row.add(searchStatus);

        return row;
    }

    private Grid<ProductCategoryEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(ProductCategoryEntity.class);

        grid.addColumn(new ComponentRenderer<>(productCategoryEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", productCategoryEntity.getName());

                        modalView.openDialogForEdit(tenant, productCategoryEntity);
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
                .setWidth("5%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("20%");

        grid.addColumn("sequenceEntity.prefix")
                .setHeader("Secuencia")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(productCategoryEntity -> {

                    final var active = productCategoryEntity.getActive() == 1 ? "Activo" : "Inactivo";
                    return new Text(active);
                })).setHeader("Activo")
                .setSortable(true);

        return grid;
    }

    @Override
    protected Page<ProductCategoryEntity> performSearch() {

        final var status = searchStatus.getValue();

        LOGGER.info("Search product categories for tenant: {} - page: {}, size: {}", tenant,
                pagination.getPage(), pagination.getSize());

        final var result = productCategoryService.search(tenant, status.getValue(), searchText.getValue(),
                pagination.getPage(), pagination.getSize());

        grid.setItems(result.getContent());

        return result;
    }
}
