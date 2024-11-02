package net.cavitos.workshop.domain.model.type;

public enum ProductType {

    PRODUCT("P"),
    SERVICE("S");

    private final String type;

    ProductType(String type) {
        this.type = type;
    }

    public String value() {
        return type;
    }

    public static ProductType of(String value) {

        return "P".equalsIgnoreCase(value) ? PRODUCT
                : SERVICE;
    }
}
