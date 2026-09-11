package model;

public enum DocumentType {
    FACTURA_ELECTRONICA("Factura Electrónica"),
    CONTRATO_LEGAL("Contrato Legal"),
    REPORTE_FINANCIERO("Reporte Financiero"),
    CERTIFICADO_DIGITAL("Certificado Digital"),
    DECLARACION_TRIBUTARIA("Declaración Tributaria");

    private final String nombreVisible;

    DocumentType(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }
    public String getNombreVisible() {
        return nombreVisible;
    }


}
