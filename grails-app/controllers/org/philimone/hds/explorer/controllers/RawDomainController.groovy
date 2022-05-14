package org.philimone.hds.explorer.controllers

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
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

        if (entity == RawEntity.VISIT){
            redirect action: "editVisit", params: [id: params.id]
            return
        }

        if (entity == RawEntity.INCOMPLETE_VISIT){
            redirect action: "editIncompleteVisit", params: [id: params.id]
            return
        }

        if (entity == RawEntity.MEMBER_ENUMERATION){
            redirect action: "editMemberEnu", params: [id: params.id]
            return
        }

        if (entity == RawEntity.DEATH){
            redirect action: "editDeath", params: [id: params.id]
            return
        }

        if (entity == RawEntity.OUT_MIGRATION){
            redirect action: "editOutMigration", params: [id: params.id]
            return
        }

        if (entity == RawEntity.IN_MIGRATION){
            redirect action: "editInMigration", params: [id: params.id]
            return
        }

        if (entity == RawEntity.EXTERNAL_INMIGRATION){
            redirect action: "editExternalInMigration", params: [id: params.id]
            return
        }

        if (entity == RawEntity.PREGNANCY_REGISTRATION){
            redirect action: "editPregnancyRegistration", params: [id: params.id]
            return
        }

        if (entity == RawEntity.PREGNANCY_OUTCOME){
            redirect action: "editPregnancyOutcome", params: [id: params.id]
            return
        }

        if (entity == RawEntity.MARITAL_RELATIONSHIP){
            redirect action: "editMaritalRelationship", params: [id: params.id]
            return
        }

        if (entity == RawEntity.CHANGE_HEAD_OF_HOUSEHOLD){
            redirect action: "editChangeHead", params: [id: params.id]
            return
        }

        redirect(controller: "eventSync", action:"showSyncReportDetails", model:[id: errorLog.logReportFile.id])
    }

    def editHousehold = {
        respond RawHousehold.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editVisit = {
        respond RawVisit.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editIncompleteVisit = {
        respond RawIncompleteVisit.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editMemberEnu = {
        respond RawMemberEnu.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editDeath = {
        respond RawDeath.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editOutMigration = {
        respond RawOutMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editInMigration = {
        respond RawInMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editExternalInMigration = {
        respond RawExternalInMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editPregnancyRegistration = {
        respond RawPregnancyRegistration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editPregnancyOutcome = {
        respond RawPregnancyOutcome.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editMaritalRelationship = {
        respond RawMaritalRelationship.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
    }

    def editChangeHead = {
        respond RawChangeHead.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.get(params.id)?.messages]
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

    def updateVisit = {

        RawVisit rawVisit = RawVisit.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.visitDate = StringUtil.toLocalDateFromDate(params.visitDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawVisit, params)
            rawVisit.save(flush:true)

            if (reset) {
                //reset the event
                RawVisit.executeUpdate("update RawVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawVisit.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawVisit.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawVisit.id])
            }

        } catch (ValidationException e) {
            respond rawVisit.errors, view:'editVisit', model: [mode: "edit", errorMessages: RawErrorLog.get(rawVisit.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawVisit.label', default: 'Raw Visit'), rawVisit.code])
        respond rawVisit, view:"editVisit", model: [mode: "show"]

    }

    def updateIncompleteVisit = {

        RawIncompleteVisit rawIncompleteVisit = RawIncompleteVisit.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawIncompleteVisit, params)
            rawIncompleteVisit.save(flush:true)

            if (reset) {
                //reset the event
                RawIncompleteVisit.executeUpdate("update RawIncompleteVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawIncompleteVisit.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawIncompleteVisit.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawIncompleteVisit.id])
            }

        } catch (ValidationException e) {
            respond rawIncompleteVisit.errors, view:'editIncompleteVisit', model: [mode: "edit", errorMessages: RawErrorLog.get(rawIncompleteVisit.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawIncompleteVisit.label', default: 'Raw Incomplete Visit'), rawIncompleteVisit.visitCode])
        respond rawIncompleteVisit, view:"editIncompleteVisit", model: [mode: "show"]

    }

    def updateMemberEnu = {

        RawMemberEnu rawMemberEnu = RawMemberEnu.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.dob = StringUtil.toLocalDateFromDate(params.dob)
        params.residencyStartDate = StringUtil.toLocalDateFromDate(params.residencyStartDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawMemberEnu, params)
            rawMemberEnu.save(flush:true)

            if (reset) {
                //reset the event
                RawMemberEnu.executeUpdate("update RawMemberEnu r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMemberEnu.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMemberEnu.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawMemberEnu.id])
            }

        } catch (ValidationException e) {
            respond rawMemberEnu.errors, view:'editMemberEnu', model: [mode: "edit", errorMessages: RawErrorLog.get(rawMemberEnu.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawMemberEnu.label', default: 'Raw Member Enu'), rawMemberEnu.code])
        respond rawMemberEnu, view:"editMemberEnu", model: [mode: "show"]

    }

    def updateDeath = {

        RawDeath rawDeath = RawDeath.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.deathDate = StringUtil.toLocalDateFromDate(params.deathDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawDeath, params)
            rawDeath.save(flush:true)

            if (reset) {
                //reset the event
                RawDeath.executeUpdate("update RawDeath r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawDeath.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawDeath.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawDeath.id])
            }

        } catch (ValidationException e) {
            respond rawDeath.errors, view:'editDeath', model: [mode: "edit", errorMessages: RawErrorLog.get(rawDeath.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawDeath.label', default: 'Raw Death'), rawDeath.memberCode])
        respond rawDeath, view:"editDeath", model: [mode: "show"]

    }

    def updateOutMigration = {

        RawOutMigration rawOutMigration = RawOutMigration.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.migrationDate = StringUtil.toLocalDateFromDate(params.migrationDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawOutMigration, params)
            rawOutMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawOutMigration.executeUpdate("update RawOutMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawOutMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawOutMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawOutMigration.id])
            }

        } catch (ValidationException e) {
            respond rawOutMigration.errors, view:'editOutMigration', model: [mode: "edit", errorMessages: RawErrorLog.get(rawOutMigration.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawOutMigration.label', default: 'Raw Out Migration'), rawOutMigration.memberCode])
        respond rawOutMigration, view:"editOutMigration", model: [mode: "show"]

    }

    def updateInMigration = {

        RawInMigration rawInMigration = RawInMigration.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.migrationDate = StringUtil.toLocalDateFromDate(params.migrationDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawInMigration, params)
            rawInMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawInMigration.executeUpdate("update RawInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawInMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawInMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawInMigration.id])
            }

        } catch (ValidationException e) {
            respond rawInMigration.errors, view:'editInMigration', model: [mode: "edit", errorMessages: RawErrorLog.get(rawInMigration.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawInMigration.label', default: 'Raw In Migration'), rawInMigration.memberCode])
        respond rawInMigration, view:"editInMigration", model: [mode: "show"]

    }

    def updateExternalInMigration = {

        RawExternalInMigration rawExternalInMigration = RawExternalInMigration.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.memberDob = StringUtil.toLocalDateFromDate(params.memberDob)
        params.migrationDate = StringUtil.toLocalDateFromDate(params.migrationDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawExternalInMigration, params)
            rawExternalInMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawExternalInMigration.executeUpdate("update RawExternalInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawExternalInMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawExternalInMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawExternalInMigration.id])
            }

        } catch (ValidationException e) {
            respond rawExternalInMigration.errors, view:'editExternalInMigration', model: [mode: "edit", errorMessages: RawErrorLog.get(rawExternalInMigration.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawExternalInMigration.label', default: 'Raw External In Migration'), rawExternalInMigration.memberCode])
        respond rawExternalInMigration, view:"editExternalInMigration", model: [mode: "show"]

    }

    def updatePregnancyRegistration = {

        RawPregnancyRegistration rawPregnancyRegistration = RawPregnancyRegistration.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.recordedDate = StringUtil.toLocalDateFromDate(params.recordedDate)
        params.eddDate = StringUtil.toLocalDateFromDate(params.eddDate)
        params.lmpDate = StringUtil.toLocalDateFromDate(params.lmpDate)
        params.expectedDeliveryDate = StringUtil.toLocalDateFromDate(params.expectedDeliveryDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawPregnancyRegistration, params)
            rawPregnancyRegistration.save(flush:true)

            if (reset) {
                //reset the event
                RawPregnancyRegistration.executeUpdate("update RawPregnancyRegistration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyRegistration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyRegistration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawPregnancyRegistration.id])
            }

        } catch (ValidationException e) {
            respond rawPregnancyRegistration.errors, view:'editPregnancyRegistration', model: [mode: "edit", errorMessages: RawErrorLog.get(rawPregnancyRegistration.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawPregnancyRegistration.label', default: 'Raw Pregnancy Registration'), rawPregnancyRegistration.code])
        respond rawPregnancyRegistration, view:"editPregnancyRegistration", model: [mode: "show"]

    }

    def updatePregnancyOutcome = {

        RawPregnancyOutcome rawPregnancyOutcome = RawPregnancyOutcome.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.outcomeDate = StringUtil.toLocalDateFromDate(params.outcomeDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawPregnancyOutcome, params)
            rawPregnancyOutcome.save(flush:true)

            if (reset) {
                //reset the event
                RawPregnancyOutcome.executeUpdate("update RawPregnancyOutcome r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyOutcome.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyOutcome.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawPregnancyOutcome.id])
            }

        } catch (ValidationException e) {
            respond rawPregnancyOutcome.errors, view:'editPregnancyOutcome', model: [mode: "edit", errorMessages: RawErrorLog.get(rawPregnancyOutcome.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawPregnancyOutcome.label', default: 'Raw Pregnancy Outcome'), rawPregnancyOutcome.code])
        respond rawPregnancyOutcome, view:"editPregnancyOutcome", model: [mode: "show"]

    }

    def updateMaritalRelationship = {

        RawMaritalRelationship rawMaritalRelationship = RawMaritalRelationship.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.startDate = StringUtil.toLocalDateFromDate(params.startDate)
        params.endDate = StringUtil.toLocalDateFromDate(params.endDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawMaritalRelationship, params)
            rawMaritalRelationship.save(flush:true)

            if (reset) {
                //reset the event
                RawMaritalRelationship.executeUpdate("update RawMaritalRelationship r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMaritalRelationship.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMaritalRelationship.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawMaritalRelationship.id])
            }

        } catch (ValidationException e) {
            respond rawMaritalRelationship.errors, view:'editMaritalRelationship', model: [mode: "edit", errorMessages: RawErrorLog.get(rawMaritalRelationship.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawMaritalRelationship.label', default: 'Raw Marital Relationship'), rawMaritalRelationship.memberA])
        respond rawMaritalRelationship, view:"editMaritalRelationship", model: [mode: "show"]

    }

    def updateChangeHead = {

        RawChangeHead rawChangeHead = RawChangeHead.get(params.id)

        def reset = false

        if (params.reset){
            reset = params.reset
        }

        params.eventDate = StringUtil.toLocalDateFromDate(params.eventDate)
        params.collectedDate = StringUtil.toLocalDateTime(params.collectedDate)
        params.uploadedDate = StringUtil.toLocalDateTime(params.uploadedDate)

        try {
            bindData(rawChangeHead, params)
            rawChangeHead.save(flush:true)

            if (reset) {
                //reset the event
                RawChangeHead.executeUpdate("update RawChangeHead r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawChangeHead.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawChangeHead.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?", [rawChangeHead.id])
            }

        } catch (ValidationException e) {
            respond rawChangeHead.errors, view:'editChangeHead', model: [mode: "edit", errorMessages: RawErrorLog.get(rawChangeHead.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawChangeHead.label', default: 'Raw Change Head'), rawChangeHead.newHeadCode])
        respond rawChangeHead, view:"editChangeHead", model: [mode: "show"]

    }
}
