package com.evox.evox.model.enums;

public enum TypeOfIdentification {
    CEDULA("Cédula"),
    PASAPORTE("Pasaporte"),
    CEDULA_EXTRANJERA("Cédula extranjera"),
    DNI ("Documento nacional de identidad"),
    LICENCIA_CONDUCCION("Licencia de Conducción");

    private final String description;

    TypeOfIdentification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
