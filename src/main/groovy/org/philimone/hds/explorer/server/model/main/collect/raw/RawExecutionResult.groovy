package org.philimone.hds.explorer.server.model.main.collect.raw

class RawExecutionResult<D> {
    enum Status {
        SUCCESS, SEMI_ERROR, ERROR
    }

    Status status
    List<RawMessage> errorMessages = new ArrayList<>();
    D domainInstance //Result of Execution

    RawExecutionResult(Status status, List<RawMessage> errorMessages) {
        this.status = status
        if (errorMessages != null) { this.errorMessages.addAll(errorMessages) }
    }

    RawExecutionResult(Status status, D instance, List<RawMessage> errorMessages) {
        this.status = status
        this.domainInstance = instance
        if (errorMessages != null) { this.errorMessages.addAll(errorMessages) }
    }

    static RawExecutionResult<D> newResult(Status status, List<RawMessage> errorMessages){
        new RawExecutionResult(status, errorMessages)
    }

    static RawExecutionResult<D> newSuccessResult(){
        new RawExecutionResult(Status.SUCCESS, null)
    }

    static RawExecutionResult<D> newSuccessResult(D instance){
        new RawExecutionResult(Status.SUCCESS, instance,null)
    }

    static RawExecutionResult<D> newSuccessResult(D instance, List<RawMessage> otherErrors){
        new RawExecutionResult(Status.SUCCESS, instance, otherErrors)
    }

    static RawExecutionResult<D> newErrorResult(List<RawMessage> errorMessages){
        new RawExecutionResult(Status.ERROR, errorMessages)
    }

    static RawExecutionResult<D> newSemiErrorResult(List<RawMessage> errorMessages){
        new RawExecutionResult(Status.SEMI_ERROR, errorMessages)
    }
}
