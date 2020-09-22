package org.philimone.hds.explorer.server.io.database.postgresql

import org.hibernate.dialect.PostgreSQL94Dialect

class CustomPostgreSQLDialect extends PostgreSQL94Dialect {
    @Override
    Class getNativeIdentifierGeneratorClass() {
        PerTableSequenceGenerator.class
    }
    String getNativeIdentifierGeneratorStrategy() {
        return "org.philimone.hds.explorer.server.io.database.postgresql.PerTableSequenceGenerator";
    }

}
