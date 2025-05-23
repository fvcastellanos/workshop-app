package net.cavitos.workshop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.cavitos.workshop.sequence.model.entity.SequenceEntity;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_category")
public class ProductCategoryEntity {

    @Id
    @Size(max = 50)
    private String id;

    @NotEmpty
    @Size(max = 50)
    private String code;

    @NotEmpty
    @Size(max = 150)
    private String name;

    @Size(max = 300)
    private String description;

    private int active;

    private Instant created;

    private Instant updated;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @ManyToOne
    @JoinColumn(name = "sequence_id")
    private SequenceEntity sequenceEntity;
}
