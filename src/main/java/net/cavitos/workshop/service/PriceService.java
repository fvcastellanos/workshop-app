package net.cavitos.workshop.service;

import org.springframework.stereotype.Service;

@Service
public class PriceService {
    
    private final ApplicationConfigurationService configurationService;

    public PriceService(final ApplicationConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public double calculatePrice(final double price) {

        // final var configuration = configurationService.getConfiguration(null)

        return 0;
    }
}
