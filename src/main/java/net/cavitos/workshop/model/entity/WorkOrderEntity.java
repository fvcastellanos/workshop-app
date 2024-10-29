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
@Table(name = "work_order")
public class WorkOrderEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "car_line_id")
    private CarLineEntity carLineEntity;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private ContactEntity contactEntity;

    @NotEmpty
    @Size(max = 100)
    private String number;

    @NotNull
    private Instant orderDate;

    @NotEmpty
    @Size(max = 1)
    private String status;

    @NotEmpty
    @Size(max = 1)
    private String odometerMeasurement;

    @Min(0)
    @NotNull
    private double odometerValue;

    @Min(0)
    @NotNull
    private double gasAmount;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @NotEmpty
    @Size(max = 30)
    private String plateNumber;

    private String notes;

    @NotNull
    @CreatedDate
    private Instant created;

    @NotNull
    private Instant updated;
}
