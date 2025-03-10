package net.cavitos.workshop.views.product;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
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
import net.cavitos.workshop.model.entity.ProductStockEntity;
import net.cavitos.workshop.security.service.DatabaseUserService;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.layouts.CRUDLayout;
import net.cavitos.workshop.views.layouts.MainLayout;
import net.cavitos.workshop.views.model.TypeOption;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@PageTitle("Existencias")
@RolesAllowed({ "ROLE_user" })
@Route(value = "stock", layout = MainLayout.class)
public class ProductStockView extends CRUDLayout {

    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    private final List<TypeOption> productCategories;

    private final Grid<ProductStockEntity> grid;

    private TextField searchTextField;
    private Select<TypeOption> categoryType;

    public ProductStockView(final AuthenticationContext authenticationContext,
                            final DatabaseUserService databaseUserService,
                            final ProductCategoryService productCategoryService,
                            final ProductService productService) {

        super(authenticationContext, databaseUserService);

        this.productCategoryService = productCategoryService;
        this.productService = productService;

        this.productCategories = loadProductCategories();

        this.grid = buildGrid();

        add(
                ComponentFactory.buildSearchTitle("Búsqueda"),
                buildSearchBox(),
                grid
        );

        performSearch();
    }

    @Override
    protected Page<ProductStockEntity> performSearch() {

        final var text = searchTextField.getValue();
        final var category = categoryType.getValue();

        final var result = productService.getProductStock(text, category.getValue(), tenant, 0, Integer.MAX_VALUE);

        grid.setItems(result.getContent());

        return result;
    }

    private VerticalLayout buildSearchBox() {

        final var searchBox = ComponentFactory.buildSearchBox();

        searchBox.add(
                buildSearchBody(),
                buildSearchFooter()
        );

        return searchBox;
    }

    private HorizontalLayout buildSearchFooter() {

        final var searchButton = new Button("Buscar", event -> performSearch());
        searchButton.setWidth("min-content");

        final var searchFooter = ComponentFactory.buildSearchFooter();
        searchFooter.add(
                searchButton
        );

        return searchFooter;
    }

    private HorizontalLayout buildSearchBody() {

        final var categories = new ArrayList<TypeOption>();
        categories.addFirst(new TypeOption("Todas", "%"));
        categories.addAll(productCategories);

        searchTextField = ComponentFactory.buildTextSearchField("70%");
        categoryType = ComponentFactory.buildTypeSelect("30%", "Categorías", categories, "%");

        final var searchBody = ComponentFactory.buildSearchBody();
        searchBody.add(searchTextField, categoryType);

        return searchBody;
    }

    private List<TypeOption> loadProductCategories() {

        return productCategoryService.getProductCategories(tenant, 1)
                .stream()
                .map(category -> new TypeOption(category.getName(), category.getId()))
                .toList();
    }

    private Grid<ProductStockEntity> buildGrid() {

        final var grid = ComponentFactory.buildGrid(ProductStockEntity.class);

        grid.addColumn(new ComponentRenderer<>(stockEntity -> {

                final var product = stockEntity.getProductEntity();

                final var layout = new HorizontalLayout();
                layout.setWidthFull();
                layout.setJustifyContentMode(JustifyContentMode.CENTER);

                final var lowStockImage = new Image("img/icons/delivery-truck-ui-svgrepo-com.svg", "Low Stock");
                lowStockImage.setWidth("20px");
                lowStockImage.setHeight("20px");
                lowStockImage.getStyle().set("cursor", "pointer");
                    lowStockImage.getStyle().set("filter", "invert(75%) sepia(100%) saturate(1000%) hue-rotate(0deg) brightness(100%) contrast(100%)");

                final var displayStockWarning = !(stockEntity.getTotal() > product.getMinimalQuantity());
                lowStockImage.setVisible(displayStockWarning);

                layout.add(lowStockImage);

                return layout;
            }))
                .setHeader("#")
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("id.code")
                .setHeader("Código")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("name")
                .setHeader("Nombre")
                .setSortable(true)
                .setResizable(true)
                .setWidth("25%");

        grid.addColumn(new ComponentRenderer<>(stockEntity -> {

                    final var product = stockEntity.getProductEntity();
                    final var category = product.getProductCategoryEntity();

                    return nonNull(category) ? new Text(category.getName())
                            : new Text(StringUtils.EMPTY);

                })).setHeader("Categoría")
                .setSortable(true)
                .setResizable(true)
                .setWidth("15%");

        grid.addColumn("productEntity.minimalQuantity")
                .setHeader("Cant Mín")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        grid.addColumn("total")
                .setHeader("Existencias")
                .setSortable(true)
                .setResizable(true)
                .setWidth("10%");

        return grid;
    }
}
