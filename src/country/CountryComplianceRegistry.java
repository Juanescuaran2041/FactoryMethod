package country;

import exception.UnsupportedCountryException;
import model.CountryCode;

import java.util.EnumMap;
import java.util.Map;

/**
 * Singleton: single, application-wide access point to the {@link CountryComplianceFactory}
 * for each supported country. A registry like this is a textbook Singleton use case because
 * building the five factories is cheap but must happen exactly once and be shared by every
 * request thread instead of re-created per upload.
 */
public final class CountryComplianceRegistry {

    private static final CountryComplianceRegistry INSTANCE = new CountryComplianceRegistry();

    private final Map<CountryCode, CountryComplianceFactory> factories = new EnumMap<>(CountryCode.class);

    private CountryComplianceRegistry() {
        register(new ColombiaComplianceFactory());
        register(new MexicoComplianceFactory());
        register(new BrazilComplianceFactory());
        register(new SpainComplianceFactory());
        register(new UnitedStatesComplianceFactory());
    }

    public static CountryComplianceRegistry getInstance() {
        return INSTANCE;
    }

    private void register(CountryComplianceFactory factory) {
        factories.put(factory.getCountryCode(), factory);
    }

    public CountryComplianceFactory getFactory(CountryCode countryCode) throws UnsupportedCountryException {
        CountryComplianceFactory factory = factories.get(countryCode);
        if (factory == null) {
            throw new UnsupportedCountryException("GlobalDocs does not yet operate in " + countryCode + ".");
        }
        return factory;
    }
}
