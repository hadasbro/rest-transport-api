package github.hadasbro.transport.classes;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.spi.MetadataBuildingContext;

import java.util.Locale;

/**
 * ImplicitNamingStrategyImpl
 *
 * use this class to force "snake" column naming in DB/jpa
 *
 * example:
 * application.proparties:
 * spring.jpa.hibernate.implicit_naming_strategy=ImplicitNamingStrategyImpl
 *
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ImplicitNamingStrategyImpl extends ImplicitNamingStrategyJpaCompliantImpl {

    @Override
    protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
        return super.toIdentifier(addUnderscores(stringForm), buildingContext);
    }

    protected static String addUnderscores(String name) {
        final StringBuilder buf = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1))
                    && Character.isUpperCase(buf.charAt(i))
                    && Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase(Locale.ROOT);
    }
}