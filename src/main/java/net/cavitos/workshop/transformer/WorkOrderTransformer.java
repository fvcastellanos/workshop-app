package net.cavitos.workshop.transformer;

import net.cavitos.workshop.domain.model.status.WorkOrderStatus;
import net.cavitos.workshop.domain.model.web.WorkOrder;
import net.cavitos.workshop.domain.model.web.common.CommonCarLine;
import net.cavitos.workshop.domain.model.web.common.CommonContact;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;

import static java.util.Objects.nonNull;
import static net.cavitos.workshop.factory.DateTimeFactory.buildStringFromInstant;

public final class WorkOrderTransformer {

    private WorkOrderTransformer() {
    }

    public static WorkOrder toWeb(WorkOrderEntity entity) {

        final var status = WorkOrderStatus.of(entity.getStatus())
                .name();

        final var workOrder = new WorkOrder();
        workOrder.setNumber(entity.getNumber());
        workOrder.setStatus(status);
        workOrder.setOrderDate(buildStringFromInstant(entity.getOrderDate()));
        workOrder.setOdometerMeasurement(entity.getOdometerMeasurement());
        workOrder.setOdometerValue(entity.getOdometerValue());
        workOrder.setGasAmount(entity.getGasAmount());
        workOrder.setNotes(entity.getNotes());
        workOrder.setPlateNumber(entity.getPlateNumber());

        if (nonNull(entity.getContactEntity())) {

            workOrder.setContact(buildWorkOrderContact(entity.getContactEntity()));
        }

        if (nonNull(entity.getCarLineEntity())) {

            workOrder.setCarLine(buildWorkOrderCarLine(entity.getCarLineEntity()));
        }

        return workOrder;
    }

    // ------------------------------------------------------------------------------------------------------

    private static CommonContact buildWorkOrderContact(final ContactEntity contactEntity) {

        final var contact = new CommonContact();
        contact.setId(contactEntity.getId());
        contact.setType(contactEntity.getType());
        contact.setName(contactEntity.getName());
        contact.setTaxId(contactEntity.getTaxId());

        return contact;
    }

    private static CommonCarLine buildWorkOrderCarLine(final CarLineEntity entity) {

        final var carLine = new CommonCarLine();
        carLine.setId(entity.getId());
        carLine.setName(entity.getName());

        return carLine;
    }
}
