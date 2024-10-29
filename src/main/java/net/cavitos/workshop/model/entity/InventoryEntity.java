package net.cavitos.workshop.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "inventory")
public class InventoryEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @ManyToOne
    @JoinColumn(name = "invoice_detail_id")
    private InvoiceDetailEntity invoiceDetailEntity;

    @ManyToOne
    @JoinColumn(name = "operation_type_id")
    private InventoryMovementTypeEntity inventoryMovementTypeEntity;

    @NotNull
    @Column(name = "operation_date")
    private Instant operationDate;

    @Size(max = 200)
    private String description;

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

    @NotNull
    @Min(value = 0)
    private double total;

    @NotNull
    @CreatedDate
    private Instant created;

    @NotNull
    @LastModifiedDate
    private Instant updated;

    @NotBlank
    @Size(max = 50)
    private String tenant;
}
