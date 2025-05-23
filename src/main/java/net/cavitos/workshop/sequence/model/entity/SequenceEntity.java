package net.cavitos.workshop.sequence.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private String id;

    @Column(name = "pad_size")
    private int padSize;

    @Column(name = "step_size")
    private int stepSize;

    @NotEmpty
    @Size(max = 5)
    private String prefix;

    @NotNull
    private long value;

    @Size(max = 300)
    private String description;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    private Instant updated;

    private Instant created;
}
