package net.cavitos.workshop.service;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.cavitos.workshop.domain.model.configuration.ConfigurationModel;
import net.cavitos.workshop.factory.BusinessExceptionFactory;
import net.cavitos.workshop.model.repository.ApplicationConfigurationRepository;

@Service
public class ApplicationConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfigurationService.class);

    private final ApplicationConfigurationRepository configurationRepository;
    private final ObjectMapper objectMapper;

    public ApplicationConfigurationService(ApplicationConfigurationRepository applicationConfigurationRepository,
                                           ObjectMapper objectMapper) {
        this.configurationRepository = applicationConfigurationRepository;
        this.objectMapper = objectMapper;
    }

    public ConfigurationModel getConfiguration(String tenant) {

        LOGGER.info("Fetching configuration for tenant: {}", tenant);

        return configurationRepository.findById(tenant)
                .map(entity -> objectMapper.convertValue(entity, ConfigurationModel.class))
                .orElseThrow(() -> BusinessExceptionFactory.createBusinessException("Configuration not found"));
    }
}
