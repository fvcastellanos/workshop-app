package net.cavitos.workshop.event.model;

import lombok.Builder;
import lombok.Getter;
import net.cavitos.workshop.model.entity.WorkOrderDetailEntity;

@Getter
@Builder
public class WorkOrderDetailEvent {
    
    private EventType eventType;
    private WorkOrderDetailEntity workOrderDetailEntity;
}
