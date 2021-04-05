package org.philimone.hds.explorer.server.model.main.collect.raw

import org.philimone.hds.explorer.server.model.enums.RawEntity

class RawMessage implements Serializable {
    RawEntity domainEntity
    String text
    String[] columns

    RawMessage(String text, String[] columns) {
        this.text = text
        this.columns = columns
    }

    RawMessage(String text, List<String> columns) {
        this.text = text
        this.columns = columns?.toArray(new String[0])
    }

    RawMessage(RawEntity entity, String text, String[] columns) {
        this.domainEntity = entity
        this.text = text
        this.columns = columns
    }

    RawMessage(RawEntity entity, String text, List<String> columns) {
        this.domainEntity = entity
        this.text = text
        this.columns = columns?.toArray(new String[0])
    }

    boolean entiltyEqualsTo(RawEntity otherEntity){
        return this.domainEntity?.equals(otherEntity)
    }
}
