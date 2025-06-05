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
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static net.cavitos.workshop.views.factory.ComponentFactory.*;

@PageTitle("Productos")
@RolesAllowed({ "ROLE_user" })
@Route(value = "products", layout = MainLayout.class)
public class ProductView extends CRUDLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductView.class);

    private final Grid<ProductEntity> grid;

    private final List<TypeOption> productCategories;

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    private final ProductModalView addModelDialog;

    private TextField searchText;
    private Select<Status> searchStatus;
    private Select<TypeOption> searchType;
    private Select<TypeOption> categoryType;

    public ProductView(final AuthenticationContext authenticationContext,
                       final DatabaseUserService databaseUserService,
                       final ProductService productService,
                       final ProductCategoryService productCategoryService,
                       final ProductModalView addModelDialog) {

        super(authenticationContext, databaseUserService);

        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.productCategories = loadProductCategories();

        this.grid = buildGrid();

        this.addModelDialog = addModelDialog;

        add(buildSearchTitle("Búsqueda"));
        add(buildSearchBox());
        add(grid);

        addModelDialog.addOnSaveEvent(productEntity -> performSearch());

        performSearch();
    }

    private VerticalLayout buildSearchBox() {

        final var btnSearch = new Button("Buscar", event -> {

            performSearch();
        });

        btnSearch.setWidth("min-content");

        final var btnAdd = new Button("Agregar Producto", event -> {
            addModelDialog.openDialogForNew(getUserTenant());
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

    private VerticalLayout buildSearchBody() {

        final var contactTypes = List.of(
                new TypeOption("Todos", "%"),
                new TypeOption("Almacenable", "Y"),
                new TypeOption("No almacenable", "N")
        );

        final var categories = new ArrayList<TypeOption>();
        categories.addFirst(new TypeOption("Todas", "%"));
        categories.addAll(productCategories);

        searchText = buildTextSearchField("100%");
        searchStatus = buildStatusSelect("50%", StatusTransformer.toView(1));
        searchType = buildTypeSelect("50%", "Tipo", contactTypes, "%");
        categoryType = buildTypeSelect("50%", "Categorías", categories, "%");

        final var row1 = ComponentFactory.buildSearchBody();
        row1.add(searchText);

        final var row2 = ComponentFactory.buildSearchBody();
        row2.add(searchType);
        row2.add(categoryType);

        final var row3 = ComponentFactory.buildSearchBody();
        row3.add(searchStatus);

        return new VerticalLayout(
                row1,
                row2,
                row3
        );
    }

    private Grid<ProductEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(ProductEntity.class);

        grid.addColumn(new ComponentRenderer<>(productEntity -> {
                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    final var editImage = new Image("img/icons/edit-3-svgrepo-com.svg", "Editar");
                    editImage.setWidth("20px");
                    editImage.setHeight("20px");
                    editImage.getStyle().set("cursor", "pointer");
                    editImage.addClickListener(event -> {
                        LOGGER.info("Edit: {}", productEntity.getName());

                        addModelDialog.openDialogForEdit(getUserTenant(), productEntity);
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
                .setWidth("10%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("30%");

        grid.addColumn(new ComponentRenderer<>(productEntity -> {

                    final var category = productEntity.getProductCategoryEntity();

                    return nonNull(category) ? new Text(category.getName())
                            : new Text(StringUtils.EMPTY);

                })).setHeader("Categoría")
                .setSortable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(productEntity -> {

                    final var layout = new HorizontalLayout();
                    layout.setWidthFull();
                    layout.setJustifyContentMode(JustifyContentMode.CENTER);

                    if (productEntity.isStorable()) {

                        final var editImage = new Image("img/icons/check-svgrepo-com.svg", "Almacenable");
                        editImage.setWidth("20px");
                        editImage.setHeight("20px");

                        layout.add(editImage);
                    }

                    return layout;

                })).setHeader("Almacenable")
                .setSortable(true)
                .setWidth("10%");

        grid.addColumn(new ComponentRenderer<>(contactEntity -> {

                    final var active = contactEntity.getActive() == 1 ? "Activo" : "Inactivo";
                    return new Text(active);
                })).setHeader("Activo")
                .setSortable(true);

        return grid;
    }

    protected Page<ProductEntity> performSearch() {

        LOGGER.info("Search products for text: {} - Status: {} - Type: {}", searchText.getValue(),
                searchStatus.getValue(), searchType.getValue());

        final var tenant = getUserTenant();
        final var type = searchType.getValue();
        final var status = searchStatus.getValue();
        final var category = categoryType.getValue();

        final var result = productService.search(tenant, type.getValue(), category.getValue(), searchText.getValue(), status.getValue(),
                DEFAULT_PAGE, DEFAULT_SIZE);

        grid.setItems(result.getContent());

        return result;
    }

    private List<TypeOption> loadProductCategories() {

        return productCategoryService.getProductCategories(tenant, 1)
                .stream()
                .map(category -> new TypeOption(category.getName(), category.getId()))
                .toList();
    }
}
