package model;

/**
 * Countries currently supported by GlobalDocs Solutions. Each country has its own
 * regulatory rules, enforced through the Abstract Factory in the {@code country} package.
 */
public enum CountryCode {
    COLOMBIA("CO", "Colombia"),
    MEXICO("MX", "Mexico"),
    BRAZIL("BR", "Brazil"),
    SPAIN("ES", "Spain"),
    UNITED_STATES("US", "United States");

    private final String isoCode;
    private final String displayName;

    CountryCode(String isoCode, String displayName) {
        this.isoCode = isoCode;
        this.displayName = displayName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CountryCode fromCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new IllegalArgumentException("Country code must not be empty");
        }
        String normalized = rawCode.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        for (CountryCode country : values()) {
            if (country.name().equals(normalized) || country.isoCode.equals(normalized)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Unknown or unsupported country: " + rawCode);
    }
}
