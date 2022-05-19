package org.philimone.hds.explorer.server.model.main.collect.raw

import org.philimone.hds.explorer.server.model.enums.RawEntity

class RawDependencyStatus {
    RawEntity entity
    boolean solved
    List<RawMessage> errorMessages = new ArrayList<>();

    RawDependencyStatus(RawEntity entity, boolean dependencySolved, List<RawMessage> errorMessages) {
        this.entity = entity
        this.solved = dependencySolved
        if (!this.solved && errorMessages != null) { this.errorMessages.addAll(errorMessages) }
    }

    static RawDependencyStatus dependencySolved(RawEntity entity) {
        return new RawDependencyStatus(entity, true, null)
    }

    static RawDependencyStatus dependencyNotSolved(RawEntity entity, List<RawMessage> errorMessages) {
        return new RawDependencyStatus(entity, true, errorMessages)
    }
}
