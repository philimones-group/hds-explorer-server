package org.philimone.hds.explorer.server.io.database.postgresql

import org.hibernate.MappingException
import org.hibernate.id.enhanced.SequenceStyleGenerator
import org.hibernate.service.ServiceRegistry
import org.hibernate.type.Type

class PerTableSequenceGenerator extends SequenceStyleGenerator {
    @Override
    void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        if (!params.getProperty(SEQUENCE_PARAM)) {
            String tableName = params.getProperty(TABLE)
            if (tableName) {
                params.setProperty(SEQUENCE_PARAM, "seq_${tableName}")
            }
        }
        super.configure(type, params, serviceRegistry)
    }
}
