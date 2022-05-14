package org.philimone.hds.explorer.controllers

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import org.philimone.hds.explorer.server.model.collect.raw.RawErrorLog
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity

@Transactional
class RawDomainController {

    def editDomain = {

        //println "id=" + params.id

        def errorLog = RawErrorLog.get(params.id)
        def entity = errorLog.entity

        if (entity == RawEntity.HOUSEHOLD){
            redirect action: "editHousehold", params: [id: params.id]
            return
        }

        redirect(controller: "eventSync", action:"showSyncReportDetails", model:[id: errorLog.logReportFile.id])
    }

    def editHousehold = {
        respond RawHousehold.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def updateHousehold = {

        RawHousehold rawHousehold = RawHousehold.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        try {
            bindData(rawHousehold, params)
            rawHousehold.save(flush:true)

            if (reset) {
                //reset the event
                RawHousehold.executeUpdate("update RawHousehold r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawHousehold.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawHousehold.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawHousehold.id])
            }

        } catch (ValidationException e) {
            respond rawHousehold.errors, view:'editHousehold', model: [mode: "edit", errorMessages: RawErrorLog.get(rawHousehold.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawHousehold.label', default: 'Raw Household'), rawHousehold.householdCode])
        respond rawHousehold, view:"editHousehold", model: [mode: "show"]

    }
}
