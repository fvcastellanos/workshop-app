package net.cavitos.workshop.sequence.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "sequence")
public class SequenceEntity {

    @Id
    private long id;

    @NotEmpty
    @Size(max = 5)
    private String prefix;

    @NotEmpty
    @Size(max = 30)
    private String value;

    private Instant updated;
}
