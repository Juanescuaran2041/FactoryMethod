package country;

/**
 * Product of the {@link CountryComplianceFactory} abstract factory: validates the format
 * of a fiscal/tax identifier (NIT, RFC, CNPJ, NIF, EIN, ...) for one specific country.
 */
public interface TaxIdValidator {

    boolean isValid(String taxId);

    /** Human-readable description of the expected format, used in rejection messages. */
    String describeExpectedFormat();
}
