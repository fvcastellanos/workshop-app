package net.cavitos.workshop.views.product;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.ProductCategory;
import net.cavitos.workshop.model.entity.ProductCategoryEntity;
import net.cavitos.workshop.service.ProductCategoryService;
import net.cavitos.workshop.transformer.ProductCategoryTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.Status;
import net.cavitos.workshop.views.model.transformer.StatusTransformer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductCategoryModalView extends DialogBase<ProductCategoryEntity> {

    private final ProductCategoryService productCategoryService;
    private final Binder<ProductCategory> binder;

    private Select<Status> statusField;
    private TextField nameField;
    private TextArea descriptionField;
    private TextField codeField;

    private ProductCategoryEntity productCategoryEntity;

    public ProductCategoryModalView(final ProductCategoryService productCategoryService) {
        super();

        this.productCategoryService = productCategoryService;
        this.binder = new Binder<>(ProductCategory.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, ProductCategoryEntity entity) {

        this.isEdit = isEdit;
        this.setHeaderTitle(isEdit ? "Modificar Categoría" : "Agregar Categoría");

        binder.refreshFields();

        statusField.setReadOnly(!isEdit);
        statusField.setValue(StatusTransformer.toView(1)); // Active status

        codeField.setVisible(isEdit);

        this.tenant = tenant;

        if (isEdit) {

            productCategoryEntity = entity;
            binder.readBean(ProductCategoryTransformer.toWeb(entity));
        }

        this.open();
    }

    private void buildContent() {

        setWidth("40%");

        codeField = new TextField("Código");
        codeField.setWidth("100%");
        codeField.setReadOnly(true);

        nameField = new TextField("Nombre");
        nameField.setWidth("100%");
        nameField.setAutofocus(true);

        descriptionField = new TextArea("Descripción");
        descriptionField.setWidth("100%");

        statusField = ComponentFactory.buildStatusSelect("100%", StatusTransformer.toView(1));

        final var contentLayout = new VerticalLayout(
                codeField,
                nameField,
                descriptionField,
                statusField
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> this.close()),
                ComponentFactory.buildSaveDialogButton(event -> this.saveChanges())
        );

        add(contentLayout, footerLayout);

        bindComponents();
    }

    private void bindComponents() {
        binder.forField(codeField)
                .bind(ProductCategory::getCode, ProductCategory::setCode);

        binder.forField(nameField)
                .asRequired("El nombre es requerido")
                .withValidator(name -> name.length() >= 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(ProductCategory::getName, ProductCategory::setName);

        binder.forField(descriptionField)
                .withValidator(description -> description.length() <= 300, "Longitud máxima 300 caracteres")
                .bind(ProductCategory::getDescription, ProductCategory::setDescription);

        binder.forField(statusField)
                .asRequired("El estado es requerido")
                .withConverter(StatusTransformer::toDomain, StatusTransformer::toView)
                .bind(ProductCategory::getActive, ProductCategory::setActive);
    }

    private void saveChanges() {

        final var validationResult = binder.validate();

        if (validationResult.isOk()) {

            try {
                final var productCategory = new ProductCategory();
                binder.writeBeanIfValid(productCategory);

                final var entity = isEdit ? productCategoryService.update(tenant, productCategoryEntity.getId(), productCategory)
                        : productCategoryService.add(tenant, productCategory);

                if (nonNull(onSaveEvent)) {

                    onSaveEvent.accept(entity);
                }

                close();

            } catch (final Exception exception) {
                showErrorNotification(exception.getMessage());
            }
        }
    }
}
