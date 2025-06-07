package net.cavitos.workshop.views.order;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.WorkOrder;
import net.cavitos.workshop.domain.model.web.common.CommonCarLine;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.service.CarLineService;
import net.cavitos.workshop.service.ContactService;
import net.cavitos.workshop.service.WorkOrderService;
import net.cavitos.workshop.transformer.WorkOrderTransformer;
import net.cavitos.workshop.views.DialogBase;
import net.cavitos.workshop.views.factory.ComponentFactory;
import net.cavitos.workshop.views.model.TypeOption;
import net.cavitos.workshop.views.model.transformer.DateTransformer;
import net.cavitos.workshop.views.model.transformer.FuelLevelTransformer;
import net.cavitos.workshop.views.model.transformer.OdometerValueTransformer;
import net.cavitos.workshop.views.model.transformer.TypeTransformer;
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
public class WorkOrderModalView extends DialogBase<WorkOrderEntity> {

    private final static Logger LOGGER = LoggerFactory.getLogger(WorkOrderModalView.class);

    private final WorkOrderService workOrderService;
    private final CarLineService carLineService;
    private final ContactService contactService;

    private final Binder<WorkOrder> binder;

    private WorkOrderEntity workOrderEntity;

    private TextField workOrderNumber;
    private DatePicker workOrderDate;
    private TextField carPlate;
    private RadioButtonGroup<TypeOption> odometerType;
    private IntegerField odometerValue;
    private Select<TypeOption> fuelLevel;
    private Select<TypeOption> status;
    private ComboBox<CommonCarLine> carModel;
    private ComboBox<CommonContact> contact;
    private TextArea notes;

    public WorkOrderModalView(final WorkOrderService workOrderService,
                              final CarLineService carLineService,
                              final ContactService contactService) {
        super();

        this.workOrderService = workOrderService;
        this.carLineService = carLineService;
        this.contactService = contactService;

        this.binder = new Binder<>(WorkOrder.class);

        buildContent();
    }

    @Override
    protected void openDialog(boolean isEdit, String tenant, WorkOrderEntity entity) {

        this.isEdit = isEdit;
        setHeaderTitle(isEdit ? "Modificar Orden de Trabajo" : "Agregar Orden de Trabajo");

        this.tenant = tenant;

        binder.refreshFields();

        setCarLineComboBoxItems();
        setContactComboBoxItems();

        odometerType.setValue(TypeTransformer.toDistanceMeasurementView("K"));
        fuelLevel.setValue(FuelLevelTransformer.toView(0D));
        status.setValue(TypeTransformer.toWorkOrderStatusView("P"));

        if (isEdit) {
            workOrderEntity = entity;
            binder.readBean(WorkOrderTransformer.toWeb(entity));
        }

        open();
    }

    private void buildContent() {

        final var odometerTypes = List.of(
                new TypeOption("Kilómetros", "K"),
                new TypeOption("Millas", "M")
        );

        final var fuelLevels = List.of(
                new TypeOption("Vacío", "0"),
                new TypeOption("1/4", "0.25"),
                new TypeOption("1/2", "0.5"),
                new TypeOption("3/4", "0.75"),
                new TypeOption("Lleno", "1")
        );

        final var orderStatuses = List.of(
                new TypeOption("En Proceso", "P"),
                new TypeOption("Cancelada", "A"),
                new TypeOption("Cerrada", "C"),
                new TypeOption("Entregada", "D")
        );

        setWidth("40%");

        workOrderNumber = new TextField("Número");
        workOrderNumber.setWidth("100%");
        workOrderNumber.setAutofocus(true);

        status = ComponentFactory.buildTypeSelect("100%", "Estado", orderStatuses, "P");
        status.setWidth("100%");
        status.setReadOnly(true);

        workOrderDate = ComponentFactory.buildDatePicker("Fecha", "100%");

        carPlate = new TextField("Placa");
        carPlate.setWidth("100%");

        odometerType = new RadioButtonGroup<>("Medida Odómetro", odometerTypes);
        odometerType.setItemLabelGenerator(TypeOption::getLabel);
        odometerType.setValue(odometerTypes.getFirst());
        odometerType.setWidth("100%");

        odometerValue = new IntegerField("Valor Odómetro");
        odometerValue.setWidth("100%");

        fuelLevel = ComponentFactory.buildTypeSelect("100%", "Nivel de Combustible", fuelLevels, "0");

        notes = new TextArea("Notas");
        notes.setWidth("100%");

        carModel = ComponentFactory.buildComboBox("Modelo", "100%", CommonCarLine::getName);

        contact = ComponentFactory.buildComboBox("Contacto", "100%", CommonContact::getName);

        final var contentLayout = new VerticalLayout(
                workOrderNumber,
                status,
                workOrderDate,
                carPlate,
                odometerType,
                odometerValue,
                fuelLevel,
                carModel,
                contact,
                notes
        );

        bindComponents();

        final var footerLayout = new HorizontalLayout(
                ComponentFactory.buildCloseDialogButton(event -> close()),
                ComponentFactory.buildSaveDialogButton(event -> saveChanges())
        );

        add(contentLayout, footerLayout);
    }

    private void setCarLineComboBoxItems() {

        final var carLines = getCarLines();
        final ComboBox.ItemFilter<CommonCarLine> itemFilter = (item, text) -> item.getName().toLowerCase()
                .contains(text.toLowerCase());
        carModel.setItems(itemFilter, carLines);
    }

    private void setContactComboBoxItems() {

        final var contacts = getContacts();
        final ComboBox.ItemFilter<CommonContact> itemFilter =
                (item, text) -> item.getName().toLowerCase()
                .contains(text.toLowerCase()) || item.getTaxId().toLowerCase()
                        .contains(text.toLowerCase());

        contact.setItems(itemFilter, contacts);
    }

    private List<CommonCarLine> getCarLines() {

        try {
            final var result = carLineService.search(tenant, "", 1, 0, 1000);

            return result.getContent()
                    .stream()
                    .map(WorkOrderTransformer::buildWorkOrderCarLine)
                    .toList();

        } catch (final Exception exception) {

            LOGGER.error("Unable to get car lines", exception);
            return Collections.emptyList();
        }
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

    private void bindComponents() {

        binder.forField(workOrderNumber)
                .asRequired("El número de orden es requerido")
                .withValidator(name -> !name.isEmpty(), "Longitud mínima 1 caracteres")
                .withValidator(name -> name.length() <= 100, "Longitud máxima 100 caracteres")
                .bind(WorkOrder::getNumber, WorkOrder::setNumber);

        binder.forField(status)
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toWorkOrderStatusView)
                .bind(WorkOrder::getStatus, WorkOrder::setStatus);

        binder.forField(workOrderDate)
                .asRequired("La fecha de la orden es requerida")
                .withConverter(DateTransformer::toDomain, DateTransformer::toView)
                .bind(WorkOrder::getOrderDate, WorkOrder::setOrderDate);

        binder.forField(carPlate)
                .asRequired("La placa del vehículo es requerida")
                .withValidator(name -> name.length() > 2, "Longitud mínima 2 caracteres")
                .withValidator(name -> name.length() <= 50, "Longitud máxima 10 caracteres")
                .bind(WorkOrder::getPlateNumber, WorkOrder::setPlateNumber);

        binder.forField(odometerType)
                .withConverter(TypeTransformer::toDomain, TypeTransformer::toDistanceMeasurementView)
                .bind(WorkOrder::getOdometerMeasurement, WorkOrder::setOdometerMeasurement);

        binder.forField(odometerValue)
                .withConverter(OdometerValueTransformer::toDomain, OdometerValueTransformer::toView)
                .bind(WorkOrder::getOdometerValue, WorkOrder::setOdometerValue);

        binder.forField(fuelLevel)
                .withConverter(FuelLevelTransformer::toDomain, FuelLevelTransformer::toView)
                .bind(WorkOrder::getGasAmount, WorkOrder::setGasAmount);

        binder.forField(carModel)
                .asRequired("El modelo del vehículo es requerido")
                .bind(WorkOrder::getCarLine, WorkOrder::setCarLine);

        binder.forField(contact)
                .asRequired("El contacto es requerido")
                .bind(WorkOrder::getContact, WorkOrder::setContact);

        binder.forField(notes)
                .withValidator(description -> description.length() <= 1024, "Longitud máxima 1024 caracteres")
                .bind(WorkOrder::getNotes, WorkOrder::setNotes);
    }

    private void saveChanges() {

        try {
            final var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var workOrder = new WorkOrder();
                binder.writeBeanIfValid(workOrder);

                final var entity = isEdit ? workOrderService.update(tenant, workOrderEntity.getId(), workOrder) :
                        workOrderService.add(tenant, workOrder);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(entity);
                }

                close();
            }

        } catch (final Exception exception) {
            LOGGER.error("Unable to save work order", exception);
            showErrorNotification(exception.getMessage());
        }
    }

}
