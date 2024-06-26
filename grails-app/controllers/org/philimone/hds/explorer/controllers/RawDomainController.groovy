package org.philimone.hds.explorer.controllers

import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import grails.converters.JSON
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Residency

@Transactional
class RawDomainController {

    def rawDomainService

    def editDomain = {

        //println "id=" + params.id

        def errorLog = RawErrorLog.findByUuid(params.id)
        def entity = errorLog?.entity

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

        redirect(controller: "eventSync", action:"showSyncReportDetails", model:[id: errorLog?.logReportFile?.id])
    }

    def editHousehold = {
        respond RawHousehold.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editVisit = {
        respond RawVisit.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editIncompleteVisit = {
        respond RawIncompleteVisit.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editMemberEnu = {
        respond RawMemberEnu.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editDeath = {
        respond RawDeath.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editOutMigration = {
        respond RawOutMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editInMigration = {
        respond RawInMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editExternalInMigration = {
        respond RawExternalInMigration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editPregnancyRegistration = {
        respond RawPregnancyRegistration.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editPregnancyOutcome = {
        respond RawPregnancyOutcome.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editMaritalRelationship = {
        respond RawMaritalRelationship.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
    }

    def editChangeHead = {
        respond RawChangeHead.get(params.id), model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(params.id)?.messages]
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
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawHousehold.id])
            }

        } catch (ValidationException e) {
            respond rawHousehold.errors, view:'editHousehold', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawHousehold.id)?.messages]
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

        params.visitDate = StringUtil.toLocalDateFromDate(params.getDate('visitDate'))

        try {
            bindData(rawVisit, params)
            rawVisit.save(flush:true)

            if (reset) {
                //reset the event
                RawVisit.executeUpdate("update RawVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawVisit.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawVisit.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawVisit.id])
            }

        } catch (ValidationException e) {
            respond rawVisit.errors, view:'editVisit', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawVisit.id)?.messages]
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

        try {
            bindData(rawIncompleteVisit, params)
            rawIncompleteVisit.save(flush:true)

            if (reset) {
                //reset the event
                RawIncompleteVisit.executeUpdate("update RawIncompleteVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawIncompleteVisit.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawIncompleteVisit.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawIncompleteVisit.id])
            }

        } catch (ValidationException e) {
            respond rawIncompleteVisit.errors, view:'editIncompleteVisit', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawIncompleteVisit.id)?.messages]
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

        params.dob = StringUtil.toLocalDateFromDate(params.getDate('dob'))
        params.residencyStartDate = StringUtil.toLocalDateFromDate(params.getDate('residencyStartDate'))

        try {
            bindData(rawMemberEnu, params)
            rawMemberEnu.save(flush:true)

            if (reset) {
                //reset the event
                RawMemberEnu.executeUpdate("update RawMemberEnu r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMemberEnu.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMemberEnu.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawMemberEnu.id])
            }

        } catch (ValidationException e) {
            respond rawMemberEnu.errors, view:'editMemberEnu', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawMemberEnu.id)?.messages]
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

        params.deathDate = StringUtil.toLocalDateFromDate(params.getDate('deathDate'))

        try {
            bindData(rawDeath, params)
            rawDeath.save(flush:true)

            if (reset) {
                //reset the event
                RawDeath.executeUpdate("update RawDeath r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawDeath.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawDeath.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawDeath.id])
            }

        } catch (ValidationException e) {
            respond rawDeath.errors, view:'editDeath', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawDeath.id)?.messages]
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

        params.migrationDate = StringUtil.toLocalDateFromDate(params.getDate('migrationDate'))

        try {
            bindData(rawOutMigration, params)
            rawOutMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawOutMigration.executeUpdate("update RawOutMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawOutMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawOutMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawOutMigration.id])
            }

        } catch (ValidationException e) {
            respond rawOutMigration.errors, view:'editOutMigration', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawOutMigration.id)?.messages]
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

        params.migrationDate = StringUtil.toLocalDateFromDate(params.getDate('migrationDate'))

        try {
            bindData(rawInMigration, params)
            rawInMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawInMigration.executeUpdate("update RawInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawInMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawInMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawInMigration.id])
            }

        } catch (ValidationException e) {
            respond rawInMigration.errors, view:'editInMigration', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawInMigration.id)?.messages]
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

        params.memberDob = StringUtil.toLocalDateFromDate(params.getDate('memberDob'))
        params.migrationDate = StringUtil.toLocalDateFromDate(params.getDate('migrationDate'))

        try {
            bindData(rawExternalInMigration, params)
            rawExternalInMigration.save(flush:true)

            if (reset) {
                //reset the event
                RawExternalInMigration.executeUpdate("update RawExternalInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawExternalInMigration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawExternalInMigration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawExternalInMigration.id])
            }

        } catch (ValidationException e) {
            respond rawExternalInMigration.errors, view:'editExternalInMigration', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawExternalInMigration.id)?.messages]
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

        params.recordedDate = StringUtil.toLocalDateFromDate(params.getDate('recordedDate'))
        params.eddDate = StringUtil.toLocalDateFromDate(params.getDate('eddDate'))
        params.lmpDate = StringUtil.toLocalDateFromDate(params.getDate('lmpDate'))
        params.expectedDeliveryDate = StringUtil.toLocalDateFromDate(params.getDate('expectedDeliveryDate'))

        try {
            bindData(rawPregnancyRegistration, params)
            rawPregnancyRegistration.save(flush:true)

            if (reset) {
                //reset the event
                RawPregnancyRegistration.executeUpdate("update RawPregnancyRegistration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyRegistration.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyRegistration.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawPregnancyRegistration.id])
            }

        } catch (ValidationException e) {
            respond rawPregnancyRegistration.errors, view:'editPregnancyRegistration', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawPregnancyRegistration.id)?.messages]
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

        params.outcomeDate = StringUtil.toLocalDateFromDate(params.getDate('outcomeDate'))

        try {
            bindData(rawPregnancyOutcome, params)
            rawPregnancyOutcome.save(flush:true)

            if (reset) {
                //reset the event
                RawPregnancyOutcome.executeUpdate("update RawPregnancyOutcome r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyOutcome.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawPregnancyOutcome.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawPregnancyOutcome.id])
            }

        } catch (ValidationException e) {
            respond rawPregnancyOutcome.errors, view:'editPregnancyOutcome', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawPregnancyOutcome.id)?.messages]
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

        params.startDate = StringUtil.toLocalDateFromDate(params.getDate('startDate'))
        params.endDate = StringUtil.toLocalDateFromDate(params.getDate('endDate'))

        try {
            bindData(rawMaritalRelationship, params)
            rawMaritalRelationship.save(flush:true)

            if (reset) {
                //reset the event
                RawMaritalRelationship.executeUpdate("update RawMaritalRelationship r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMaritalRelationship.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawMaritalRelationship.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawMaritalRelationship.id])
            }

        } catch (ValidationException e) {
            respond rawMaritalRelationship.errors, view:'editMaritalRelationship', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawMaritalRelationship.id)?.messages]
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

        params.eventDate = StringUtil.toLocalDateFromDate(params.getDate('eventDate'))

        try {
            bindData(rawChangeHead, params)
            rawChangeHead.save(flush:true)

            if (reset) {
                //reset the event
                RawChangeHead.executeUpdate("update RawChangeHead r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawChangeHead.id])
                RawEvent.executeUpdate("update RawEvent r set r.processed=:status where r.eventId=:rawId", [status: ProcessedStatus.NOT_PROCESSED, rawId: rawChangeHead.id])
                RawErrorLog.executeUpdate("delete from RawErrorLog r where r.uuid=?0", [rawChangeHead.id])
            }

        } catch (ValidationException e) {
            respond rawChangeHead.errors, view:'editChangeHead', model: [mode: "edit", errorMessages: RawErrorLog.findByUuid(rawChangeHead.id)?.messages]
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawChangeHead.label', default: 'Raw Change Head'), rawChangeHead.newHeadCode])
        respond rawChangeHead, view:"editChangeHead", model: [mode: "show"]

    }

    def invalidateHousehold = {
        RawHousehold rawHousehold = RawHousehold.get(params.id)
        RawHousehold.executeUpdate("update RawHousehold r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawHousehold.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawHousehold.label', default: 'Raw Household'), rawHousehold.householdCode])
        respond rawHousehold, view:"editHousehold", model: [mode: "show"]
    }

    def invalidateVisit = {
        RawVisit rawVisit = RawVisit.get(params.id)
        RawVisit.executeUpdate("update RawVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawVisit.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawVisit.label', default: 'Raw Visit'), rawVisit.code])
        respond rawVisit, view:"editVisit", model: [mode: "show"]
    }

    def invalidateIncompleteVisit = {
        RawIncompleteVisit rawIncompleteVisit = RawIncompleteVisit.get(params.id)
        RawIncompleteVisit.executeUpdate("update RawIncompleteVisit r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawIncompleteVisit.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawIncompleteVisit.label', default: 'Raw Incomplete Visit'), rawIncompleteVisit.visitCode])
        respond rawIncompleteVisit, view:"editIncompleteVisit", model: [mode: "show"]
    }

    def invalidateMemberEnu = {
        RawMemberEnu rawMemberEnu = RawMemberEnu.get(params.id)
        RawMemberEnu.executeUpdate("update RawMemberEnu r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawMemberEnu.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawMemberEnu.label', default: 'Raw Member Enu'), rawMemberEnu.code])
        respond rawMemberEnu, view:"editMemberEnu", model: [mode: "show"]
    }

    def invalidateDeath = {
        RawDeath rawDeath = RawDeath.get(params.id)
        RawDeath.executeUpdate("update RawDeath r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawDeath.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawDeath.label', default: 'Raw Death'), rawDeath.memberCode])
        respond rawDeath, view:"editDeath", model: [mode: "show"]
    }

    def invalidateOutMigration = {
        RawOutMigration rawOutMigration = RawOutMigration.get(params.id)
        RawOutMigration.executeUpdate("update RawOutMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawOutMigration.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawOutMigration.label', default: 'Raw Out Migration'), rawOutMigration.memberCode])
        respond rawOutMigration, view:"editOutMigration", model: [mode: "show"]
    }

    def invalidateInMigration = {
        RawInMigration rawInMigration = RawInMigration.get(params.id)
        RawInMigration.executeUpdate("update RawInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawInMigration.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawInMigration.label', default: 'Raw In Migration'), rawInMigration.memberCode])
        respond rawInMigration, view:"editInMigration", model: [mode: "show"]
    }

    def invalidateExternalInMigration = {
        RawExternalInMigration rawExternalInMigration = RawExternalInMigration.get(params.id)
        RawExternalInMigration.executeUpdate("update RawExternalInMigration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawExternalInMigration.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawExternalInMigration.label', default: 'Raw External In Migration'), rawExternalInMigration.memberCode])
        respond rawExternalInMigration, view:"editExternalInMigration", model: [mode: "show"]
    }

    def invalidatePregnancyRegistration = {
        RawPregnancyRegistration rawPregnancyRegistration = RawPregnancyRegistration.get(params.id)
        RawPregnancyRegistration.executeUpdate("update RawPregnancyRegistration r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawPregnancyRegistration.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawPregnancyRegistration.label', default: 'Raw Pregnancy Registration'), rawPregnancyRegistration.code])
        respond rawPregnancyRegistration, view:"editPregnancyRegistration", model: [mode: "show"]
    }

    def invalidatePregnancyOutcome = {
        RawPregnancyOutcome rawPregnancyOutcome = RawPregnancyOutcome.get(params.id)
        RawPregnancyOutcome.executeUpdate("update RawPregnancyOutcome r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawPregnancyOutcome.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawPregnancyOutcome.label', default: 'Raw Pregnancy Outcome'), rawPregnancyOutcome.code])
        respond rawPregnancyOutcome, view:"editPregnancyOutcome", model: [mode: "show"]
    }

    def invalidateMaritalRelationship = {
        RawMaritalRelationship rawMaritalRelationship = RawMaritalRelationship.get(params.id)
        RawMaritalRelationship.executeUpdate("update RawMaritalRelationship r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawMaritalRelationship.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawMaritalRelationship.label', default: 'Raw Marital Relationship'), rawMaritalRelationship.memberA])
        respond rawMaritalRelationship, view:"editMaritalRelationship", model: [mode: "show"]
    }

    def invalidateChangeHead = {
        RawChangeHead rawChangeHead = RawChangeHead.get(params.id)
        RawChangeHead.executeUpdate("update RawChangeHead r set r.processedStatus=:status where r.id=:rawId", [status: ProcessedStatus.INVALIDATED, rawId: rawChangeHead.id])

        flash.message = message(code: 'default.updated.message', args: [message(code: 'rawChangeHead.label', default: 'Raw Change Head'), rawChangeHead.newHeadCode])
        respond rawChangeHead, view:"editChangeHead", model: [mode: "show"]
    }

    def residenciesList = {

        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def household_code = rawDomainService.getHouseholdCode(params.id)

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }

        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            //createAlias('endType', 'endTypeAlias')
            or {
                member {
                    or {
                        if (search_filter) ilike 'code', search_filter
                        if (search_filter) ilike 'name', search_filter
                        if (search_filter) ilike 'gender', search_filter
                    }
                }

                if (search_filter) ilike 'startType', search_filter
                if (search_filter) ilike 'endType', search_filter

            }
        }

        //def residencies = Residency.findAllByHouseholdCodeAndEndType(household_code, ResidencyEndType.NOT_APPLICABLE)
        def residencies = Residency.withCriteria {
            filterer.delegate = delegate
            filterer()

            eq("householdCode", household_code)
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }

        //println "household=${household_code}, ${residencies?.size()}"

        //Display records
        def objects = residencies.collect { residency ->
            ['code':     residency.memberCode,
             'name':     residency.member.name,
             'gender':   residency.member.gender?.code,
             'dob':      StringUtil.formatLocalDate(residency.member.dob),
             'startType':       residency.startType?.code,
             'startDate':       StringUtil.formatLocalDate(residency.startDate),
             'endType':         residency.endType?.code,
             'endDate':         StringUtil.formatLocalDate(residency.endDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = Residency.countByHouseholdCode(household_code)
        def recordsFiltered = Residency.withCriteria {
            filterer.delegate = delegate
            filterer()

            eq("householdCode", household_code)
        }.size()


        //println "member recordsTotal $recordsTotal"
        //println "member recordsFiltered $recordsFiltered"
        //println "members ${members.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: objects]

        render result as JSON
    }

    def headRelationshipsList = {

        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def household_code = rawDomainService.getHouseholdCode(params.id)

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }


        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            or {
                if (search_filter) ilike 'member.code', search_filter
                if (search_filter) ilike 'member.name', search_filter
                if (search_filter) ilike 'member.gender', search_filter
                if (search_filter) ilike 'relationshipType', search_filter
                if (search_filter) ilike 'startType', search_filter
                if (search_filter) ilike 'endType', search_filter

            }
        }

        //def results = HeadRelationship.findAllByHouseholdCodeAndEndType(household_code, HeadRelationshipEndType.NOT_APPLICABLE)
        def results = HeadRelationship.withCriteria {
            filterer.delegate = delegate
            filterer()

            eq("householdCode", household_code)
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }

        //println "household=${household_code}, ${results?.size()}"

        //Display records
        def objects = results.collect { obj ->
            ['code':     obj.memberCode,
             'name':     obj.member.name,
             'gender':   obj.member.gender?.code,
             'dob':      StringUtil.formatLocalDate(obj.member.dob),
             'headRelationshipType': obj.relationshipType?.code,
             'startType':       obj.startType?.code,
             'startDate':       StringUtil.formatLocalDate(obj.startDate),
             'endType':         obj.endType?.code,
             'endDate':         StringUtil.formatLocalDate(obj.endDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = HeadRelationship.countByHouseholdCode(household_code)
        def recordsFiltered = HeadRelationship.withCriteria {
            eq("householdCode", household_code)

            filterer.delegate = delegate
            filterer()
        }.size()


        //println "member recordsTotal $recordsTotal"
        //println "member recordsFiltered $recordsFiltered"
        //println "members ${members.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: objects]

        render result as JSON
    }

    def maritalRelationshipsList = {
        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def member_code = params.id

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }

        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            //createAlias('endType', 'endTypeAlias')
            or {
                if (search_filter) ilike 'memberA_code', search_filter
                if (search_filter) ilike 'memberB_code', search_filter
            }
        }

        def relationships = MaritalRelationship.withCriteria {
            filterer.delegate = delegate
            filterer()

            or {
                eq("memberA_code", member_code)
                eq("memberB_code", member_code)
            }

            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }

        println "household=${member_code}, ${relationships?.size()}"

        //Display records memberA_code, memberB_code, isPolygamic, startStatus, startDate, endStatus, endDate
        def objects = relationships.collect { relationship ->
            ['memberA_code':     relationship.memberA_code,
             'memberB_code':     relationship.memberB_code,
             'isPolygamic':   relationship.isPolygamic==true,
             'startStatus':       relationship.startStatus?.code,
             'startDate':       StringUtil.formatLocalDate(relationship.startDate),
             'endStatus':         relationship.endStatus?.code,
             'endDate':         StringUtil.formatLocalDate(relationship.endDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = MaritalRelationship.countByMemberA_codeOrMemberB_code(member_code, member_code)
        def recordsFiltered = MaritalRelationship.withCriteria {
            filterer.delegate = delegate
            filterer()

            or {
                eq("memberA_code", member_code)
                eq("memberB_code", member_code)
            }
        }.size()


        println "member recordsTotal $recordsTotal"
        //println "member recordsFiltered $recordsFiltered"
        //println "members ${members.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: objects]

        render result as JSON
    }
}
