package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.model.web.WorkOrder;
import net.cavitos.workshop.model.entity.CarLineEntity;
import net.cavitos.workshop.model.entity.ContactEntity;
import net.cavitos.workshop.model.entity.WorkOrderEntity;
import net.cavitos.workshop.model.generator.TimeBasedGenerator;
import net.cavitos.workshop.model.repository.CarLineRepository;
import net.cavitos.workshop.model.repository.ContactRepository;
import net.cavitos.workshop.model.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static net.cavitos.workshop.domain.model.status.WorkOrderStatus.IN_PROGRESS;
import static net.cavitos.workshop.factory.BusinessExceptionFactory.createBusinessException;
import static net.cavitos.workshop.factory.DateTimeFactory.buildInstantFrom;

@Service
public class WorkOrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(WorkOrderService.class);

    private final WorkOrderRepository workOrderRepository;
    private final CarLineRepository carLineRepository;
    private final ContactRepository contactRepository;

    public WorkOrderService(final WorkOrderRepository workOrderRepository,
                            final CarLineRepository carLineRepository,
                            final ContactRepository contactRepository) {

        this.workOrderRepository = workOrderRepository;
        this.carLineRepository = carLineRepository;
        this.contactRepository = contactRepository;
    }

    public Page<WorkOrderEntity> search(final String tenant,
                                        final String text,
                                        final String status,
                                        int page,
                                        int size) {

        LOGGER.info("Search for work orders with text={}, status={} for tenant={}", text, status, tenant);

        final var pageable = PageRequest.of(page, size);
        return workOrderRepository.search(tenant, status, "%" + text + "%", pageable);
    }

    public WorkOrderEntity findById(final String tenant, final String id) {

        LOGGER.info("Retrieve work_order_id={} for tenant={}", id, tenant);

        final var entity = workOrderRepository.findById(id)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Work Order not found"));

        if (!entity.getTenant().equalsIgnoreCase(tenant)) {

            LOGGER.error("work_order_id={} is not associated with tenant={}", id, tenant);
            throw createBusinessException(HttpStatus.NOT_FOUND, "Work Order not found");
        }

        return entity;
    }

    public WorkOrderEntity add(final String tenant, final WorkOrder workOrder) {

        LOGGER.info("Adding a new work_order_number={} for tenant={}", workOrder.getNumber(), tenant);

        verifyWorkOrderNumberAlreadyExists(tenant, workOrder);

        final var carLineEntity = getCarLine(tenant, workOrder);
        final var contactEntity = getContact(tenant, workOrder);

        final var entity = WorkOrderEntity.builder()
                .id(TimeBasedGenerator.generateTimeBasedId())
                .carLineEntity(carLineEntity)
                .contactEntity(contactEntity)
                .status(IN_PROGRESS.value())
                .orderDate(buildInstantFrom(workOrder.getOrderDate()))
                .odometerMeasurement(workOrder.getOdometerMeasurement())
                .odometerValue(workOrder.getOdometerValue())
                .gasAmount(workOrder.getGasAmount())
                .number(workOrder.getNumber())
                .notes(workOrder.getNotes())
                .tenant(tenant)
                .plateNumber(workOrder.getPlateNumber())
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        workOrderRepository.save(entity);

        return entity;
    }

    public WorkOrderEntity update(final String tenant, final String id, final WorkOrder workOrder) {

        LOGGER.info("Update work_order_id={} for tenant={}", id, tenant);

        final var entity = workOrderRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.NOT_FOUND, "Work Order not found"));

        if (!entity.getNumber().equalsIgnoreCase(workOrder.getNumber())) {

            verifyWorkOrderNumberAlreadyExists(tenant, workOrder);
        }

        final var contactEntity = getContact(tenant, workOrder);
        final var carLineEntity = getCarLine(tenant, workOrder);

        entity.setContactEntity(contactEntity);
        entity.setCarLineEntity(carLineEntity);
        entity.setNumber(workOrder.getNumber());
        entity.setNotes(workOrder.getNotes());
        entity.setGasAmount(workOrder.getGasAmount());
        entity.setOdometerMeasurement(workOrder.getOdometerMeasurement());
        entity.setOdometerValue(workOrder.getOdometerValue());
        entity.setStatus(workOrder.getStatus());
        entity.setPlateNumber(workOrder.getPlateNumber());
        entity.setUpdated(Instant.now());

        workOrderRepository.save(entity);

        return entity;
    }

    // ----------------------------------------------------------------------------------------------------

    private void verifyWorkOrderNumberAlreadyExists(final String tenant, final WorkOrder workOrder) {

        final var workOrderHolder = workOrderRepository.findByNumberEqualsIgnoreCaseAndTenant(workOrder.getNumber(),
                tenant);

        if (workOrderHolder.isPresent()) {

            LOGGER.error("work_order_number={} already exists for tenant={}", workOrder.getNumber(), tenant);
            throw createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Work Order Number already exists");
        }
    }

    private CarLineEntity getCarLine(final String tenant, final WorkOrder workOrder) {

        final var carLine = workOrder.getCarLine();
        return carLineRepository.findByIdAndTenant(carLine.getId(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Car Line not found"));
    }

    private ContactEntity getContact(final String tenant, final WorkOrder workOrder) {

        final var contact = workOrder.getContact();
        return contactRepository.findByIdAndTenant(contact.getId(), tenant)
                .orElseThrow(() -> createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Contact not found"));
    }
}
