package net.cavitos.workshop.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "work_order_detail")
public class WorkOrderDetailEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "work_order_id")
    private WorkOrderEntity workOrderEntity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "invoice_detail_id")
    private InvoiceDetailEntity invoiceDetailEntity;

    private String description;

    private String notes;

    @NotNull
    @Min(value = 0)
    private double quantity;

    @NotNull
    @Min(value = 0)
    private double unitPrice;

    @NotNull
    @Min(value = 0)
    @Column(name = "sale_price")
    private double salePrice;

    @Column(name = "operation_date")
    private Instant operationDate;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @NotNull
    @CreatedDate
    private Instant created;
}
