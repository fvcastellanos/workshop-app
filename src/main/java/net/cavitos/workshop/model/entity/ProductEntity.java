package net.cavitos.workshop.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@Table(name = "product")
public class ProductEntity {

    @Id
    @Size(max = 50)
    private String id;

    @NotEmpty
    @Size(max = 50)
    private String code;

    private boolean storable;

    @NotEmpty
    @Size(max = 150)
    private String name;

    @Size(max = 300)
    private String description;

    @Column(name = "minimal_quantity")
    private double minimalQuantity;

    @Column(name = "sale_price")
    private double salePrice;

    @CreatedDate
    private Instant created;

    private Instant updated;

    private int active;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @ManyToOne
    @JoinColumn(name = "product_category_id")
    private ProductCategoryEntity productCategoryEntity;
}
