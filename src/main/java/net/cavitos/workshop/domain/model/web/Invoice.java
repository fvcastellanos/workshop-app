package net.cavitos.workshop.domain.model.web;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.workshop.domain.model.web.common.CommonContact;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Invoice {
    
    private String type;
    private String suffix;
    private String number;
    private String imageUrl;
    private String invoiceDate;
    private String effectiveDate;
    private String status;
    private CommonContact contact;
}
