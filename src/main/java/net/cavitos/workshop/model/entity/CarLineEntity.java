package net.cavitos.workshop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "car_line")
public class CarLineEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    private CarBrandEntity carBrand;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @Size(max = 300)
    private String description;

    private int active;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @CreatedDate
    private Instant created;
}
