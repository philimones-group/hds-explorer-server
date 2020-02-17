package org.philimone.hds.explorer.server.model.json

class SyncProcessedStatus {
    int code
    String name
    int totalRecords
    int processed
    int processedWithError
    int notProcessed
    int invalidated
    int otherCases //normally temporary cases

    Date lastSync
}
