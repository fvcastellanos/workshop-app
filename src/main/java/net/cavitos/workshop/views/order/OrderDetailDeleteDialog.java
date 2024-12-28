package net.cavitos.workshop.views.order;

import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;
import net.cavitos.workshop.service.WorkOrderDetailService;
import net.cavitos.workshop.views.component.DeleteDialog;

public class OrderDetailDeleteDialog extends DeleteDialog<WorkOrderDetailEntity> {

    private final WorkOrderDetailService workOrderDetailService;

    public OrderDetailDeleteDialog(final WorkOrderDetailService workOrderDetailService) {
        super();
        this.workOrderDetailService = workOrderDetailService;
    }

    @Override
    protected String getEntityName() {

        final var entity = getEntity();



        return "";
    }

    @Override
    protected void deleteEntity(WorkOrderDetailEntity entity) {

//        workOrderDetailService.deleteOrderDetail(deleteOrderDetail);
    }
}
