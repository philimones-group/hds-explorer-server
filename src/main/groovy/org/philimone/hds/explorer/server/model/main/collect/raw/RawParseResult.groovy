package org.philimone.hds.explorer.server.model.main.collect.raw

class RawParseResult<D> {

    List<RawMessage> errorMessages = new ArrayList<>();
    D domainInstance //Result of Execution


    RawParseResult(D instance, List<RawMessage> errorMessages) {
        this.domainInstance = instance
        if (errorMessages != null) { this.errorMessages.addAll(errorMessages) }
    }

    boolean hasErrors(){
        return !this.errorMessages.empty
    }

    String getErrorsText(){
        def err = ""

        errorMessages.each {
            err += it.text +"\n"
        }

        return err
    }
}
