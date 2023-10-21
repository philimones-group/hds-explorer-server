package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.SyncEntity

import java.time.LocalDateTime

@Transactional
class SyncFilesReportService {

    def update(SyncEntity entity, int totalRecords) {

        def currentReport = SyncFilesReport.findByCode(entity.code)
        if (currentReport == null) {
            currentReport = new SyncFilesReport()
            currentReport.code = entity.code
        }

        currentReport.name = entity
        currentReport.records = totalRecords
        currentReport.syncDate = LocalDateTime.now()

        currentReport.save(flush:true)
    }

    def SyncFilesReport get(String code){

        if (code == null) return null

        def entity = SyncEntity.values().find{ it.name() == code}

        if (entity == null) return null

        SyncFilesReport.findByName(entity)
    }
}
