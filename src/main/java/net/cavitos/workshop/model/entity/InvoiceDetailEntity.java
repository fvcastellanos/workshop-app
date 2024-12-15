package net.cavitos.workshop.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "invoice_detail")
public class InvoiceDetailEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoiceEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "work_order_id")
    private WorkOrderEntity workOrderEntity;

    @NotNull
    @Min(value = 0)
    private double quantity;

    @NotNull
    @Min(value = 0)
    @Column(name = "unit_price")
    private double unitPrice;

    @Min(value = 0)
    @Column(name = "discount_amount")
    private double discountAmount;

    @Transient
    private double discountPercentage;

    @Transient
    private double total;

    @NotEmpty
    private String tenant;

    private Instant created;

    public double getDiscountPercentage() {
        return discountAmount / (quantity * unitPrice);
    }

    public double getTotal() {
        return (quantity * unitPrice) - discountAmount;
    }
}
