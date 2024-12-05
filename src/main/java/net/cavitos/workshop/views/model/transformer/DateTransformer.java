package net.cavitos.workshop.views.model.transformer;

import net.cavitos.workshop.domain.model.web.WorkOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateTransformer {

    private DateTransformer() {
    }
    
    public static String toDomain(LocalDate localDate) {

        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate toView(String orderDate) {

        return LocalDate.parse(orderDate, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
