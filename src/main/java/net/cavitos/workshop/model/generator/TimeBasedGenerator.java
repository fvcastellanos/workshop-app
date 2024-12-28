package net.cavitos.workshop.model.generator;

import com.github.ksuid.KsuidGenerator;

import java.security.SecureRandom;

public class TimeBasedGenerator {

    private static final KsuidGenerator generator = new KsuidGenerator(new SecureRandom());

    private TimeBasedGenerator() {        
    }
    
    public static String generateTimeBasedId() {

        final var ksuid = generator.newKsuid();

        return ksuid
                .toString();
    }
}
