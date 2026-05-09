package net.cavitos.workshop.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "invoice")
public class InvoiceEntity {

    @Id
    @Size(max = 50)
    private String id;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private ContactEntity contactEntity;

    @NotEmpty
    @Size(max = 1)
    private String type;

    @Size(max = 30)
    private String suffix;

    @NotEmpty
    @Size(max = 100)
    private String number;

    @Size(max = 250)
    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "invoice_date")
    private Instant invoiceDate;

    @Column(name = "effective_date")
    private Instant effectiveDate;

    @NotEmpty
    @Size(max = 50)
    private String tenant;

    @NotEmpty
    @Size(max = 1)
    private String status;

    @NotNull
    @CreatedDate
    private Instant created;

    @NotNull
    private Instant updated;
}
