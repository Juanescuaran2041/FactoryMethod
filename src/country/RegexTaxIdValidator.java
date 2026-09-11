package country;

import java.util.regex.Pattern;

/**
 * Reusable {@link TaxIdValidator} implementation configured with a regular expression.
 * Each concrete {@link CountryComplianceFactory} instantiates one of these with its own
 * country-specific pattern.
 */
public final class RegexTaxIdValidator implements TaxIdValidator {

    private final Pattern pattern;
    private final String expectedFormatDescription;

    public RegexTaxIdValidator(String regex, String expectedFormatDescription) {
        this.pattern = Pattern.compile(regex);
        this.expectedFormatDescription = expectedFormatDescription;
    }

    @Override
    public boolean isValid(String taxId) {
        return taxId != null && pattern.matcher(taxId.trim()).matches();
    }

    @Override
    public String describeExpectedFormat() {
        return expectedFormatDescription;
    }
}
