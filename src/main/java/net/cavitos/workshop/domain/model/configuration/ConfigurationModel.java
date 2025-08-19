package net.cavitos.workshop.domain.model.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class ConfigurationModel {
    
    private String tenant;
    private double salePercentage;
}
