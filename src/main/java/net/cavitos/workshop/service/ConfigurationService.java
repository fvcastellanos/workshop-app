package net.cavitos.workshop.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.cavitos.workshop.model.repository.ConfigurationRepository;

@Service
public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    private final ConfigurationRepository configurationRepository;

    public ConfigurationService(final ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public Properties getConfiguration(String tenant) {

        LOGGER.info("Fetching configuration for tenant: {}", tenant);

        final var properties = new Properties();

        configurationRepository.findAllByTenant(tenant)
            .forEach(entity -> {
                properties.put(entity.getKey(), entity.getValue());
            });

        return properties;
    }
}
