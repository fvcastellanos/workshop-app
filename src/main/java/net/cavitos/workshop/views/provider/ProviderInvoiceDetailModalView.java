package net.cavitos.workshop.views.provider;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.InvoiceDetail;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;
import net.cavitos.workshop.service.InvoiceDetailService;
import net.cavitos.workshop.service.ProductService;
import net.cavitos.workshop.transformer.InvoiceDetailTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProviderInvoiceDetailModalView extends DialogBase<InvoiceDetail> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProviderInvoiceDetailModalView.class);

    private final InvoiceDetailService invoiceDetailService;
    private final ProductService productService;

    private final Binder<InvoiceDetail> binder;

    private NumberField quantityField;
    private ComboBox<CommonProduct> productField;
    private NumberField unitPriceField;
    private NumberField discountPercentageField;
    private TextField orderNumberField;

    private InvoiceDetail invoiceDetail;
    private String invoiceId;

    public ProviderInvoiceDetailModalView(final InvoiceDetailService invoiceDetailService,
                                          final ProductService productService) {
        super();

        this.invoiceDetailService = invoiceDetailService;
        this.productService = productService;

        this.binder = new Binder<>(InvoiceDetail.class);

        buildContent();
    }

    public void setInvoiceId(final String invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, InvoiceDetail entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Detalle" : "Agregar Detalle");

        this.tenant = tenant;

        binder.refreshFields();

        setProductItems();

        if (isEdit) {

            invoiceDetail = entity;
            binder.readBean(entity);
        }

        open();
    }

    private void buildContent() {

        setWidth("60%");

        quantityField = new NumberField("Cantidad");
        quantityField.setAutofocus(true);
        quantityField.setWidth("33%");
        quantityField.setMin(1);
        quantityField.setStep(1);

        productField = ComponentFactory.buildComboBox("Producto", "33%", CommonProduct::getName);

        unitPriceField = new NumberField("Precio Unitario");
        unitPriceField.setWidth("34%");
        unitPriceField.setMin(0);

        discountPercentageField = new NumberField("Porcentaje de Descuento");
        discountPercentageField.setWidth("50%");
        discountPercentageField.setMin(0);

        orderNumberField = new TextField("Número de Orden");
        orderNumberField.setWidth("50%");

        final var row1 = new HorizontalLayout(
                quantityField,
                productField,
                unitPriceField
        );
        row1.setWidth("100%");

        final var row2 = new HorizontalLayout(
                discountPercentageField,
                orderNumberField
        );
        row2.setWidth("100%");

        final var controls = new VerticalLayout(
                row1,
                row2
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        bindComponents();

        add(controls, footerLayout);
    }

    private void bindComponents() {

        binder.forField(quantityField)
                .asRequired("La cantidad es requerida")
                .withValidator(quantity -> quantity >= 1, "La cantidad debe ser mayor o igual a 1")
                .bind(InvoiceDetail::getQuantity, InvoiceDetail::setQuantity);

        binder.forField(productField)
                .asRequired("El producto es requerido")
                .bind(InvoiceDetail::getProduct, InvoiceDetail::setProduct);

        binder.forField(unitPriceField)
                .asRequired("El precio unitario es requerido")
                .withValidator(unitPrice -> unitPrice > 0, "El precio unitario debe ser mayor a 0")
                .bind(InvoiceDetail::getUnitPrice, InvoiceDetail::setUnitPrice);

        binder.forField(discountPercentageField)
                .withValidator(discountPercentage -> isNull(discountPercentage) || discountPercentage >= 0, "El porcentaje de descuento debe ser mayor o igual a 0")
                .withValidator(discountPercentage -> isNull(discountPercentage) || discountPercentage <= 100, "El porcentaje de descuento debe ser menor o igual a 100")
                .bind(InvoiceDetail::getDiscountPercentage, (detail, value) -> {

                    if (isNull(value)) {
                        detail.setDiscountPercentage(0);
                    } else {
                        detail.setDiscountPercentage(value);
                    }
                });

        binder.forField(orderNumberField)
                .withValidator(orderNumber -> orderNumber.length() <= 50, "Longitud máxima 50 caracteres")
                .bind(InvoiceDetail::getWorkOrderNumber, InvoiceDetail::setWorkOrderNumber);
    }

    private void setProductItems() {

        final var products = loadProducts();

        ComboBox.ItemFilter<CommonProduct> filter = (product, filterString) -> {
            final var filterLower = filterString.toLowerCase();
            return product.getName().toLowerCase().contains(filterLower) || product.getCode().toLowerCase().contains(filterLower);
        };

        productField.setItems(filter, products);
    }

    private List<CommonProduct> loadProducts() {

        try {
            final var searchResult = productService.search(tenant, "%", "%", "", 1, 0, 5000);

            return searchResult.getContent()
                    .stream()
                    .map(entity -> {

                        final var product = new CommonProduct();
                        product.setCode(entity.getCode());
                        product.setType(entity.getType());
                        product.setName(entity.getName());

                        return product;
                    }).toList();

        } catch (final Exception exception) {

            LOGGER.error("Error al cargar los productos", exception);
            return Collections.emptyList();
        }
    }

    private void saveChanges() {

        try {

            final var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var detail = new InvoiceDetail();
                binder.writeBeanIfValid(detail);

                final var entity = isEdit ? invoiceDetailService.update(tenant, invoiceId, invoiceDetail.getId(), detail)
                        : invoiceDetailService.add(tenant, invoiceId, detail);

                if (nonNull(onSaveEvent)) {

                    onSaveEvent.accept(InvoiceDetailTransformer.toWeb(entity));
                }

                close();
            }
        } catch (final Exception exception) {
            LOGGER.error("Unable to save invoice detail: ", exception);
            showErrorNotification(exception.getMessage());
        }
    }
}
