package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.web.WorkOrder;
import net.cavitos.workshop.domain.model.web.common.CommonCarLine;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.factory.ZonedDateTimeFactory;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public final class WorkOrderTransformer {

    private final ZonedDateTimeFactory zonedDateTimeFactory;

    public WorkOrderTransformer(final ZonedDateTimeFactory zonedDateTimeFactory) {

        this.zonedDateTimeFactory = zonedDateTimeFactory;
    }

    public WorkOrder toWeb(WorkOrderEntity entity) {

        final var workOrder = new WorkOrder();
        workOrder.setNumber(entity.getNumber());
        workOrder.setStatus(entity.getStatus());
        workOrder.setOrderDate(zonedDateTimeFactory.buildStringFromInstant(entity.getOrderDate()));
        workOrder.setOdometerMeasurement(entity.getOdometerMeasurement());
        workOrder.setOdometerValue(entity.getOdometerValue());
        workOrder.setGasAmount(entity.getGasAmount());
        workOrder.setNotes(entity.getNotes());
        workOrder.setPlateNumber(entity.getPlateNumber());
        workOrder.setColor(entity.getColor());
        workOrder.setMakeYear(entity.getMakeYear());

        if (nonNull(entity.getContactEntity())) {

            workOrder.setContact(buildWorkOrderContact(entity.getContactEntity()));
        }

        if (nonNull(entity.getCarLineEntity())) {

            workOrder.setCarLine(buildWorkOrderCarLine(entity.getCarLineEntity()));
        }

        return workOrder;
    }

    public CommonContact buildWorkOrderContact(final ContactEntity contactEntity) {

        final var contact = new CommonContact();
        contact.setId(contactEntity.getId());
        contact.setType(contactEntity.getType());
        contact.setName(contactEntity.getName());
        contact.setTaxId(contactEntity.getTaxId());

        return contact;
    }

    public CommonCarLine buildWorkOrderCarLine(final CarLineEntity entity) {

        final var carLine = new CommonCarLine();
        carLine.setId(entity.getId());
        carLine.setName(entity.getName());

        return carLine;
    }
}
