package org.philimone.hds.explorer.server.model.main.collect.raw

import groovy.transform.CompileStatic
import org.philimone.hds.explorer.server.model.enums.RawEntity

@CompileStatic
class RawExecutionResult<D> {
    enum Status {
        SUCCESS, SEMI_ERROR, ERROR
    }

    Status status
    List<RawMessage> errorMessages = new ArrayList<>();
    D domainInstance //Result of Execution
    RawEntity entity

    RawExecutionResult(RawEntity entity, Status status, List<RawMessage> errorMessages) {
        this.entity = entity
        this.status = status
        if (errorMessages != null) { this.errorMessages.addAll(errorMessages) }
    }

    RawExecutionResult(RawEntity entity, Status status, D instance, List<RawMessage> errorMessages) {
        this(entity, status, errorMessages)
        this.domainInstance = instance
    }

    static <D> RawExecutionResult<D> newResult(RawEntity entity, Status status, List<RawMessage> errorMessages){
        new RawExecutionResult(entity, status, errorMessages)
    }

    static <D> RawExecutionResult<D> newSuccessResult(RawEntity entity){
        new RawExecutionResult(entity, Status.SUCCESS, null)
    }

    static <D> RawExecutionResult<D> newSuccessResult(RawEntity entity, D instance){
        new RawExecutionResult(entity, Status.SUCCESS, instance,null)
    }

    static <D> RawExecutionResult<D> newSuccessResult(RawEntity entity, D instance, List<RawMessage> otherErrors){
        new RawExecutionResult(entity, Status.SUCCESS, instance, otherErrors)
    }

    static <D> RawExecutionResult<D> newErrorResult(RawEntity entity, List<RawMessage> errorMessages){
        new RawExecutionResult(entity, Status.ERROR, errorMessages)
    }

    static <D> RawExecutionResult<D> newSemiErrorResult(RawEntity entity, List<RawMessage> errorMessages){
        new RawExecutionResult(entity, Status.SEMI_ERROR, errorMessages)
    }
}
