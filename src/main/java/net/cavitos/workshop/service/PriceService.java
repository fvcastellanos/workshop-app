package net.cavitos.workshop.service;

import org.springframework.stereotype.Service;

@Service
public class PriceService {
    
    private final ConfigurationService configurationService;

    public PriceService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public double calculatePrice(final double price, final String tenant) {

        final var salePercentage = 1 - getSalePercentage(tenant);
        return Math.ceil(price / salePercentage);
    }

    private double getSalePercentage(String tenant) {
        final var properties = configurationService.getConfiguration(tenant);

        final var value = properties.getProperty("sale.percentage", "0.35");

        return Double.parseDouble(value);
    }
}
