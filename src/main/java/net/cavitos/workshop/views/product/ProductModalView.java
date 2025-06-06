package net.cavitos.workshop.views.product;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductModalView extends DialogBase<ProductEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductModalView.class);

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;
    private final Binder<Product> binder;

    private Select<Status> statusField;
    private Select<TypeOption> categoryType;
    private NumberField minStockField;
    private TextField codeField;
    private Checkbox isStorableField;
    private TextField nameField;
    private TextArea descriptionField;

    private ProductEntity productEntity;

    public ProductModalView(final ProductService productService,
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

        binder.refreshFields();

        statusField.setValue(StatusTransformer.toView(1)); // Active status
        statusField.setReadOnly(!isEdit);

        isStorableField.setValue(true);

        categoryType.setItems(loadProductCategories());
        minStockField.setValue(1.0);

        codeField.setVisible(isEdit);

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

        final var initialItems = Collections.singletonList(new TypeOption("Seleccione", StringUtils.EMPTY));
        categoryType = ComponentFactory.buildTypeSelect("100%", "Categoría", initialItems, StringUtils.EMPTY);

        isStorableField = new Checkbox("¿Almacenable?");
        isStorableField.setValue(true);

        codeField = new TextField();
        codeField.setLabel("Código");
        codeField.setWidth("100%");
        codeField.setReadOnly(true);

        nameField = new TextField();
        nameField.setLabel("Nombre");
        nameField.setWidth("100%");

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        minStockField = new NumberField("Cantidad Mínima");
        minStockField.setMin(1);
        minStockField.setMax(1000);
        minStockField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        contentLayout.add(
                categoryType,
                codeField,
                nameField,
                descriptionField,
                minStockField,
                isStorableField,
                statusField
        );

        bindComponents();

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close()),
                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout);
        add(footerLayout);
    }

    private void bindComponents() {

        // Bind fields
        binder.forField(categoryType)
                .withValidator(category -> nonNull(category) && !category.getValue().equals(StringUtils.EMPTY), "Seleccione una categoría")
                .withConverter(CategoryTransformer::toDomain, CategoryTransformer::toView)
                .bind(Product::getCategory, Product::setCategory);

        binder.forField(codeField)
                .bind(Product::getCode, Product::setCode);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 50, "Longitud máxima 50 caracteres")
                .bind(Product::getName, Product::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(Product::getDescription, Product::setDescription);

        binder.forField(minStockField)
                .withValidator(minStock -> nonNull(minStock) && minStock >= 1, "Cantidad mínima 1")
                .bind(Product::getMinimalQuantity, Product::setMinimalQuantity);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(Product::getActive, Product::setActive);

        binder.forField(isStorableField)
                .bind(Product::isStorable, Product::setStorable);
    }

    private List<TypeOption> loadProductCategories() {

        return productCategoryService.getProductCategories(tenant, 1)
                .stream()
                .map(entity -> new TypeOption(entity.getName(), entity.getId()))
                .toList();
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {

                final var product = new Product();
                binder.writeBeanIfValid(product);

                final var entity = isEdit ? productService.update(tenant, productEntity.getId(), product) :
                        productService.add(tenant, product);

                if (nonNull(onSaveEvent)) {

                    onSaveEvent.accept(entity);
                }

                close();

            } catch (final Exception exception) {

                LOGGER.error("Unable to save contact", exception);
                showErrorNotification(exception.getMessage());
            }
        }
    }
}
