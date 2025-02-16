package net.cavitos.workshop.model.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.model.entity.composite.CodeTenantId;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "product_stock")
public class ProductStockEntity {

    @EmbeddedId
    private CodeTenantId id;
//    private String code;
//    private String tenant;
    private String name;
    private double total;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;
}
