package net.cavitos.workshop.views.order;

import com.vaadin.flow.data.binder.Binder;
import net.cavitos.workshop.domain.model.web.WorkOrderDetail;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.transformer.WorkOrderDetailTransformer;
import net.cavitos.workshop.views.DialogBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

public abstract class WorkOrderBaseModal extends DialogBase<WorkOrderDetail> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String workOrderId;
    protected WorkOrderDetail workOrderDetailEntity;

    protected final Binder<WorkOrderDetail> binder;

    protected final WorkOrderDetailService workOrderDetailService;

    protected WorkOrderDetailTransformer workOrderDetailTransformer;

    protected WorkOrderBaseModal(final WorkOrderDetailService workOrderDetailService,
                                 final WorkOrderDetailTransformer workOrderDetailTransformer) {

        super();

        this.binder = new Binder<>(WorkOrderDetail.class);
        this.workOrderDetailService = workOrderDetailService;
        this.workOrderDetailTransformer = workOrderDetailTransformer;
    }

    public void setWorkOrderId(final String workOrderId) {
        this.workOrderId = workOrderId;
    }

    protected void saveChanges() {

        try {

            var validationResult = binder.validate();

            if (validationResult.isOk()) {

                final var orderDetail = new WorkOrderDetail();
                binder.writeBeanIfValid(orderDetail);

                final var entity = isEdit ? workOrderDetailService.updateOrderDetail(workOrderDetailEntity.getOrderId(), workOrderDetailEntity.getId(), orderDetail, tenant)
                        : workOrderDetailService.addOrderDetail(tenant, workOrderId, orderDetail);

                if (nonNull(onSaveEvent)) {
                    onSaveEvent.accept(workOrderDetailTransformer.toWeb(entity));
                }

                close();
            }
        } catch (Exception exception) {
            logger.error("Unable to save work order detail", exception);
            showErrorNotification(exception.getMessage());
        }
    }

}
