package org.philimone.hds.explorer.server.report

class ExecTaskReport {
    int errors
    int processed

    static ExecTaskReport sum(ExecTaskReport... reports){
        def e=0, p=0

        reports.each {
            e += it.errors
            p += it.processed
        }

        new ExecTaskReport(errors: e, processed: p)
    }
}
