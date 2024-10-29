package net.cavitos.workshop.model.entity;

import jakarta.persistence.*;
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
@Table(name = "user")
public class UserEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private TenantEntity tenantEntity;

    @NotNull
    @Size(max = 50)
    private String provider;

    @NotNull
    @Size(max = 150)
    private String userId;

    @NotNull
    private int active;

    @NotNull
    @CreatedDate
    private Instant created;

    private Instant updated;
}
