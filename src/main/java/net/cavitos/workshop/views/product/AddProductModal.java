package net.cavitos.workshop.views.product;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.Contact;
import net.cavitos.workshop.domain.model.web.Product;
import net.cavitos.workshop.model.entity.ProductEntity;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.transformer.ProductTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.CategoryTransformer;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import net.cavitos.workshop.views.model.transformer.TypeTransformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AddProductModal extends DialogBase<ProductEntity> {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final Binder<Product> binder;

    private Select<Status> statusField;
    private Select<TypeOption> typeSelect;
    private Select<TypeOption> categoryType;

    private ProductEntity productEntity;

    public AddProductModal(final ProductService productService,
                           final ProductCategoryService productCategoryService) {

        this.productService = productService;
        this.productCategoryService = productCategoryService;
        this.binder = new Binder<>(Product.class);

        buildContent();
    }

    @Override
    protected void openDialog(final boolean isEdit,
                              final String tenant,
                              final ProductEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Producto" : "Agregar Producto");
        this.tenant = tenant;

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        statusField.setReadOnly(!isEdit);

        typeSelect.setValue(TypeTransformer.toProductView("P")); // Product type

        categoryType.setItems(loadProductCategories());
        categoryType.setValue(new TypeOption("Seleccione", StringUtils.EMPTY));

        if (isEdit) {
            productEntity = entity;
            binder.readBean(ProductTransformer.toWeb(productEntity));
        }

        this.open();
    }

    private void buildContent() {

        setWidth("40%");

        final var contentLayout = new VerticalLayout();
        contentLayout.setWidthFull();

        typeSelect = ComponentFactory.buildTypeSelect("100%", "Tipo", List.of(
                new TypeOption("Producto", "P"),
                new TypeOption("Servicio", "S")
        ), "P");
        typeSelect.setAutofocus(true);

        categoryType = ComponentFactory.buildTypeSelect("100%", "Categoría", Collections.emptyList(), StringUtils.EMPTY);

        final var nameField = new TextField();
        nameField.setLabel("Nombre");
        nameField.setWidth("100%");

        final var descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        final var minStockField = new IntegerField("Cantidad Mínima");
        minStockField.setMin(1);
        minStockField.setMax(1000);
        minStockField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        // Bind fields
        binder.forField(typeSelect)
                .asRequired("El tipo es requerido")
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toClientView)
                .bind(Product::getType, Product::setType);

        binder.forField(categoryType)
                .asRequired("La categoría es requerida")
                .withConverter(CategoryTransformer::toDomain, CategoryTransformer::toView)
                .bind(Product::getProductCategory, Product::setProductCategory);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 50, "Longitud máxima 50 caracteres")
                .bind(Product::getName, Product::setName);



        contentLayout.add(
                typeSelect,
                categoryType,
                nameField,
                descriptionField,
                minStockField,
                statusField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close())
//                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout);
        add(footerLayout);
    }

    private List<TypeOption> loadProductCategories() {

        final var productCategories = productCategoryService.getProductCategories(tenant, 1)
                .stream()
                .map(entity -> new TypeOption(entity.getName(), entity.getId()))
                .toList();

        final var categories = new ArrayList<>(productCategories);
        categories.addFirst(new TypeOption("Seleccione", StringUtils.EMPTY));

        return categories;
    }
}
