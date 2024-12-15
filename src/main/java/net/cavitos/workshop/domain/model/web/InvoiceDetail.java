package net.cavitos.workshop.domain.model.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonProduct;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceDetail {

    private String id;
    private String invoiceId;
    private CommonProduct product;
    private String workOrderNumber;
    private double quantity;
    private double unitPrice;
    private double discountAmount;
    private double discountPercentage;
    private double total;
}
