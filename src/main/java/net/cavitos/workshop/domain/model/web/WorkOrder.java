package net.cavitos.workshop.domain.model.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonCarLine;
import net.cavitos.workshop.domain.model.web.common.CommonContact;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WorkOrder {

    private String number;
    private String status;
    private String odometerMeasurement;
    private double odometerValue;
    private double gasAmount;
    private String notes;
    private String plateNumber;
    private String orderDate;
    private CommonCarLine carLine;
    private CommonContact contact;
}
