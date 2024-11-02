package net.cavitos.workshop.sequence.domain;

public enum SequenceType {

    CUSTOMER("C"),
    PROVIDER("P"),
    PRODUCT("PR"),
    SERVICE("SR"),
    INVENTORY_MOVEMENT("MI"),
    UNKNOWN("U");

    private final String prefix;

    SequenceType(final String prefix) {

        this.prefix = prefix;
    }

    public String getPrefix() {

        return prefix;
    }
}
