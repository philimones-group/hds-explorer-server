package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.SyncEntity

@Transactional
class SyncFilesReportService {

    def update(SyncEntity entity, int totalRecords) {

        def currentReport = SyncFilesReport.findByCode(entity.code)
        if (currentReport != null) {
            currentReport.delete(flush: true)
        }

        def report = SyncFilesReport.findOrCreateByNameAndRecords(entity, totalRecords)
        report.save(flush:true)
    }

    def SyncFilesReport get(String code){

        if (code == null) return null

        def entity = SyncEntity.values().find{ it.name() == code}

        if (entity == null) return null

        SyncFilesReport.findByName(entity)
    }
}
