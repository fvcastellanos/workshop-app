package net.cavitos.workshop.views.provider;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.Invoice;
import net.cavitos.workshop.domain.model.web.WorkOrder;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.model.entity.InvoiceEntity;
import net.cavitos.workshop.service.ContactService;
import net.cavitos.workshop.service.InvoiceService;
import net.cavitos.workshop.transformer.InvoiceTransformer;
import net.cavitos.workshop.transformer.WorkOrderTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.DateTransformer;
import net.cavitos.workshop.views.model.transformer.TypeTransformer;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
public class ProviderInvoiceModalView extends DialogBase<InvoiceEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderInvoiceModalView.class);

    private final InvoiceService invoiceService;
    private final ContactService contactService;
    private final Binder<Invoice> binder;

    private TextField invoiceNumber;
    private DatePicker invoiceDate;
    private Select<TypeOption> invoiceStatus;
    private ComboBox<CommonContact> provider;
    private Select<TypeOption> invoiceType;

    private InvoiceEntity invoiceEntity;

    public ProviderInvoiceModalView(final InvoiceService invoiceService,
                                    final ContactService contactService) {
        super();

        this.invoiceService = invoiceService;
        this.contactService = contactService;

        binder = new Binder<>(Invoice.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, InvoiceEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Editar Factura" : "Nueva Factura");
        this.tenant = tenant;

        setContactComboBoxItems();

        binder.refreshFields();

        invoiceStatus.setValue(TypeTransformer.toInvoiceView("A"));
        invoiceType.setValue(TypeTransformer.toInvoiceType("P"));

        if (isEdit) {
            invoiceEntity = entity;
            binder.readBean(InvoiceTransformer.toWeb(entity));
        }

        open();
    }

    private void buildContent() {

        final var invoiceStatuses = List.of(
                new TypeOption("Activa", "A"),
                new TypeOption("Anulada", "D"),
                new TypeOption("Cancelada", "C")
        );

        final var invoiceTypes = List.of(
                new TypeOption("Proveedor", "P"),
                new TypeOption("Cliente", "C")
        );

        setWidth("40%");

        invoiceType = ComponentFactory.buildTypeSelect("100%", "Tipo", invoiceTypes, "P");
        invoiceType.setReadOnly(true);

        provider = ComponentFactory.buildComboBox("Proveedor", "100%", CommonContact::getName);
        provider.setAutofocus(true);

        invoiceNumber = new TextField("Número");
        invoiceNumber.setWidth("100%");

        final var datePickerI18n = new DatePicker.DatePickerI18n();
        datePickerI18n.setDateFormat("yyyy-MM-dd");

        invoiceDate = new DatePicker("Fecha");
        invoiceDate.setI18n(datePickerI18n);
        invoiceDate.setWidth("100%");

        invoiceStatus = ComponentFactory.buildTypeSelect( "100%", "Estado", invoiceStatuses, "A");

        final var contentLayout = new VerticalLayout(
                provider,
                invoiceNumber,
                invoiceDate,
                invoiceStatus
        );

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        bindComponents();

        add(contentLayout, footerLayout);
    }

    private void bindComponents() {

        binder.forField(invoiceType)
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toInvoiceType)
                .bind(Invoice::getType, Invoice::setType);

        binder.forField(provider)
                .asRequired("El contacto es requerido")
                .bind(Invoice::getContact, Invoice::setContact);

        binder.forField(invoiceNumber)
                .asRequired("El número de factura es requerido")
                .withValidator(number -> number.length() >= 2, "Longitud mínima 2")
                .withValidator(number -> number.length() <= 100, "Longitud máxima 100")
                .bind(Invoice::getNumber, Invoice::setNumber);

        binder.forField(invoiceDate)
                .asRequired("La fecha es requerida")
                .withConverter(DateTransformer::toDomain, DateTransformer::toView)
                .bind(Invoice::getInvoiceDate, Invoice::setInvoiceDate);

        binder.forField(invoiceStatus)
                .asRequired("El estado de la factura es requerido")
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toInvoiceView)
                .bind(Invoice::getStatus, Invoice::setStatus);
    }

    private void setContactComboBoxItems() {

        final var contacts = getContacts();
        final ComboBox.ItemFilter<CommonContact> itemFilter =
                (item, text) -> item.getName().toLowerCase()
                        .contains(text.toLowerCase()) || item.getTaxId().toLowerCase()
                        .contains(text.toLowerCase());

        provider.setItems(itemFilter, contacts);
    }

    private List<CommonContact> getContacts() {

        try {
            final var result = contactService.search(tenant, "%", 1, "", 0, 1000);

            return result.getContent()
                    .stream()
                    .map(WorkOrderTransformer::buildWorkOrderContact)
                    .toList();

        } catch (final Exception exception) {
            LOGGER.error("Unable to get contacts", exception);
            return Collections.emptyList();
        }
    }

    private void saveChanges() {

        try {
            final var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var invoice = new Invoice();
                binder.writeBeanIfValid(invoice);

                final var entity = isEdit ? invoiceService.update(tenant, invoiceEntity.getId(), invoice) :
                        invoiceService.add(tenant, invoice);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                close();
            }
        } catch (final Exception exception) {
            LOGGER.error("Unable to save invoice", exception);
            showErrorNotification(exception.getMessage());
        }
    }
}
