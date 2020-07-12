package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.SyncEntity

@Transactional
class SyncFilesReportService {

    def update(SyncEntity entity, int totalRecords) {

        def report = SyncFilesReport.findOrCreateByName(entity)

        report.records = totalRecords

        report.save(flush:true)
    }

    def SyncFilesReport get(int code){

        def entity = SyncEntity.values().find{ it.code == code}

        if (entity == null) return null

        SyncFilesReport.findByName(entity)
    }
}
