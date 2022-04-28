package org.philimone.hds.explorer.server.model.json

class SyncProcessedStatus {
    int code
    String name
    /*main datasets*/
    int totalRecords /* saved in final dataset*/
    int totalCompiled
    /*raw data*/
    int notProcessed
    int processed
    int processedWithError
    int invalidated
    int otherCases //normally temporary cases

    Date lastSync

    String format(int value){
        return value==-1 ? "-" : "${value}"
    }
}
