package net.cavitos.workshop.domain.model.type;

public enum InvoiceType {

    PROVIDER("P"),
    CUSTOMER("C"),
    UNKNOWN("U");

    private final String type;

    InvoiceType(String type) {
        this.type = type;
    }

    public String value() {

        return type;
    }

    public static InvoiceType of(final String value) {

        return switch (value) {
            case "C" -> CUSTOMER;
            case "P" -> PROVIDER;
            default -> UNKNOWN;
        };
    }
}
