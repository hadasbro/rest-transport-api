package github.hadasbro.transport.classes;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import static com.google.common.base.CaseFormat.*;

/**
 * SnakeCaseNamingStrategy
 *
 * use this class to force "snake" column naming in DB/jpa
 *
 * example:
 * application.proparties:
 * spring.jpa.hibernate.physical_naming_strategy=SnakeCaseNamingStrategy
 *
 */
@SuppressWarnings({"unused"})
class SnakeCaseNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return new Identifier(
                UPPER_CAMEL.to(LOWER_UNDERSCORE, name.getText()),
                name.isQuoted()
        );
    }

    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        return new Identifier(
                LOWER_CAMEL.to(LOWER_UNDERSCORE, name.getText()),
                name.isQuoted()
        );
    }
}