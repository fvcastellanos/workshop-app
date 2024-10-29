package net.cavitos.workshop.model.generator;

import com.github.ksuid.Ksuid;

public class TimeBasedGenerator {

    private TimeBasedGenerator() {        
    }
    
    public static String generateTimeBasedId() {

        return Ksuid.newKsuid()
                .toString();
    }
}
