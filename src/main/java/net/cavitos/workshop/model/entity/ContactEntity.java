package net.cavitos.workshop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "contact")
public class ContactEntity {

    @Id
    @Size(max = 50)
    private String id;

    @NotEmpty
    @Size(max = 1)
    private String type;

    @NotEmpty
    @Size(max = 50)
    private String code;

    @NotEmpty
    @Size(max = 50)
    private String name;

    @Size(max = 150)
    private String contact;

    @Size(max = 50)
    private String taxId;

    @Size(max = 300)
    private String description;

    @Min(value = 0)
    @Max(value = 1)
    private int active;

    @CreatedDate
    private Instant created;
    private Instant updated;

    @NotEmpty
    @Size(max = 50)
    private String tenant;
}
