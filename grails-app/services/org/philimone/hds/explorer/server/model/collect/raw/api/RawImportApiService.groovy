package org.philimone.hds.explorer.server.model.collect.raw.api

import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBinder
import groovy.xml.slurpersupport.NodeChild
import groovy.xml.slurpersupport.Node
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditHousehold
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditMember
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditRegion
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.main.collect.raw.RawParseResult

import java.time.LocalDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional
class RawImportApiService implements DataBinder {

    def errorMessageService

    RawParseResult<RawRegion> parseRegion(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawRegion")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawRegion>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */
        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawRegion = new RawRegion(params)
        rawRegion.id = params.id

        return new RawParseResult<RawRegion>(rawRegion, errors)

    }

    RawParseResult<RawHousehold> parsePreHousehold(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawPreHousehold")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawHousehold>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        //overwrite this - with uploadedDate saved when persisting the data
        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        //println("new-id = "+params.id)

        def rawHousehold = new RawHousehold(params)
        rawHousehold.id = params.id
        rawHousehold.preRegistered = true

        return new RawParseResult<RawHousehold>(rawHousehold, errors)

    }

    RawParseResult<RawHousehold> parseHousehold(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawHousehold")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawHousehold>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        //overwrite this - with uploadedDate saved when persisting the data
        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        println("new-id = "+params.id)


        def rawHousehold = new RawHousehold(params)
        rawHousehold.id = params.id

        //find a preregistered household with the same householdCode to be overwritten with a completed household registration
        def preRegHousehold = RawHousehold.findByHouseholdCodeAndPreRegistered(rawHousehold.householdCode, true)

        if (preRegHousehold != null) {
            rawHousehold = preRegHousehold
            //rawHousehold.id = params.id

            bindData(rawHousehold, params)

            rawHousehold.processedStatus = ProcessedStatus.NOT_PROCESSED
        }

        //all uploads to rawHousehold are not pre-registered
        rawHousehold.preRegistered = false

        return new RawParseResult<RawHousehold>(rawHousehold, errors)

    }

    RawParseResult<RawMember> parseMember(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawMember")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawMember>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.dob.size() > 0) {
            params.dob = StringUtil.toLocalDate(xmlNode.dob.text())

            if (params.dob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.dob.text(), "dob"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawMember = new RawMember(params)
        rawMember.id = params.id

        return new RawParseResult<RawMember>(rawMember, errors)

    }

    RawParseResult<RawVisit> parseVisit(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawVisit")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawVisit>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.visitDate.size() > 0) {
            params.visitDate = StringUtil.toLocalDate(xmlNode.visitDate.text())

            if (params.visitDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.visitDate.text(), "visitDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawVisit = new RawVisit(params)
        rawVisit.id = params.id

        return new RawParseResult<RawVisit>(rawVisit, errors)

    }

    RawParseResult<RawMemberEnu> parseMemberEnu(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawMemberEnu")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawMemberEnu>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.dob.size() > 0) {
            params.dob = StringUtil.toLocalDate(xmlNode.dob.text())

            if (params.dob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.dob.text(), "dob"])
            }
        }

        if (xmlNode.residencyStartDate.size() > 0) {
            params.residencyStartDate = StringUtil.toLocalDate(xmlNode.residencyStartDate.text())

            if (params.residencyStartDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.residencyStartDate.text(), "residencyStartDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawMemberEnu = new RawMemberEnu(params)
        rawMemberEnu.id = params.id

        return new RawParseResult<RawMemberEnu>(rawMemberEnu, errors)

    }

    RawParseResult<RawExternalInMigration> parseExternalInMigration(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawExternalInMigration")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawExternalInMigration>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.memberDob.size() > 0) {
            params.memberDob = StringUtil.toLocalDate(xmlNode.memberDob.text())

            if (params.memberDob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.memberDob.text(), "memberDob"])
            }
        }

        if (xmlNode.migrationDate.size() > 0) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawExternalInMigration = new RawExternalInMigration(params)
        rawExternalInMigration.id = params.id

        return new RawParseResult<RawExternalInMigration>(rawExternalInMigration, errors)

    }

    RawParseResult<RawInMigration> parseInMigration(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawInMigration")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawInMigration>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.migrationDate.size() > 0) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInMigration = new RawInMigration(params)
        rawInMigration.id = params.id

        return new RawParseResult<RawInMigration>(rawInMigration, errors)

    }

    RawParseResult<RawOutMigration> parseOutMigration(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawOutMigration")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawOutMigration>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.migrationDate.size() > 0) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawOutMigration = new RawOutMigration(params)
        rawOutMigration.id = params.id

        return new RawParseResult<RawOutMigration>(rawOutMigration, errors)

    }

    RawParseResult<RawHeadRelationship> parseHeadRelationship(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawHeadRelationship")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawHeadRelationship>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.startDate.size() > 0) {
            params.startDate = StringUtil.toLocalDate(xmlNode.startDate.text())

            if (params.startDate==null) {
                //its not always required, sometimes are null
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.startDate.text(), "startDate"])
            }
        }

        if (xmlNode.endDate.size() > 0) {
            params.endDate = StringUtil.toLocalDate(xmlNode.endDate.text())

            if (params.endDate==null) {
                //its not always required, sometimes are null
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.endDate.text(), "endDate"])
            }
        }

        def rawHeadRelationship = new RawHeadRelationship(params)
        rawHeadRelationship.id = params.id

        return new RawParseResult<RawHeadRelationship>(rawHeadRelationship, errors)

    }

    RawParseResult<RawMaritalRelationship> parseMaritalRelationship(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawMaritalRelationship")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawMaritalRelationship>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        //Here the startDate and EndDate will come as eventDate
        //relationshipType, eventDate

        MaritalStartStatus startStatus = null
        MaritalEndStatus endStatus = null

        //Converting relationshipType to startStatus/endStatus
        if (xmlNode.relationshipType.size()>0){
            def type = xmlNode.relationshipType.text()

            startStatus = MaritalStartStatus.getFrom(type)
            endStatus = MaritalEndStatus.getFrom(type)

            if (startStatus != null){
                params.startStatus = startStatus.code
            }

            if (endStatus != null){
                params.endStatus = endStatus.code
            }

            if (startStatus == null && endStatus == null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.relationshipType.text(), "relationshipType"])
            }
        }

        if (xmlNode.eventDate.size() > 0) {
            def eventDate = StringUtil.toLocalDate(xmlNode.eventDate.text())

            if (eventDate != null) {
                if (startStatus != null){
                    params.startDate = eventDate
                }
                if (endStatus != null) {
                    params.endDate = eventDate
                }
            }

            if (eventDate == null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.eventDate.text(), "eventDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawMaritalRelationship = new RawMaritalRelationship(params)
        rawMaritalRelationship.id = params.id

        return new RawParseResult<RawMaritalRelationship>(rawMaritalRelationship, errors)

    }

    RawParseResult<RawPregnancyRegistration> parsePregnancyRegistration(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawPregnancyRegistration")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawPregnancyRegistration>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.recordedDate.size() > 0) {
            params.recordedDate = StringUtil.toLocalDate(xmlNode.recordedDate.text())

            if (params.recordedDate==null) {
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.recordedDate.text(), "recordedDate"])
            }
        }

        if (xmlNode.eddDate.size() > 0) {
            params.eddDate = StringUtil.toLocalDate(xmlNode.eddDate.text())

            if (params.eddDate==null) {
                //its not always required, sometimes are null
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.eddDate.text(), "eddDate"])
            }
        }

        if (xmlNode.lmpDate.size() > 0) {
            params.lmpDate = StringUtil.toLocalDate(xmlNode.lmpDate.text())

            if (params.lmpDate==null) {
                //its not always required, sometimes are null
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.lmpDate.text(), "lmpDate"])
            }
        }

        if (xmlNode.expectedDeliveryDate.size() > 0) {
            params.expectedDeliveryDate = StringUtil.toLocalDate(xmlNode.expectedDeliveryDate.text())

            if (params.expectedDeliveryDate==null) {
                //its not always required, sometimes are null
                //errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.expectedDeliveryDate.text(), "expectedDeliveryDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawPregnancyRegistration = new RawPregnancyRegistration(params)
        rawPregnancyRegistration.id = params.id

        return new RawParseResult<RawPregnancyRegistration>(rawPregnancyRegistration, errors)

    }

    RawParseResult<RawPregnancyOutcome> parsePregnancyOutcome(NodeChild xmlNode) {

        //has a especial handler

        def errors = new ArrayList<RawMessage>()
        def params = [:]
        def paramsChild = new ArrayList<LinkedHashMap>()
        def rootnode = xmlNode?.name()

        xmlNode.childNodes().each { Node node ->

            if (node.childNodes().size()>0 && node.name().equalsIgnoreCase("childs")){
                //println "children ${node.name()}"

                node.childNodes().eachWithIndex { Node child, index ->

                    if (child.name().equalsIgnoreCase("rawPregnancyChild")){
                        def cparams = [:]
                        child.childNodes().each { innernode ->
                            cparams.put(innernode.name(), innernode.text())
                        }

                        paramsChild.add(cparams)
                    }

                }
            } else {
                params.put(node.name(), node.text())
            }
        }

        if (!rootnode.equalsIgnoreCase("RawPregnancyOutcome")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawPregnancyOutcome>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode?.outcomeDate.size() > 0) {
            params.outcomeDate = StringUtil.toLocalDate(xmlNode.outcomeDate.text())

            if (params.outcomeDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.outcomeDate.text(), "outcomeDate"])
            }
        }

        if (xmlNode?.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode?.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawPregnancyOutcome(params)
        rawInstance.id = params.id

        if (paramsChild.size() > 0) {
            paramsChild.each { cparams ->
                def rawChild = new RawPregnancyChild(cparams)
                rawChild.outcome = rawInstance
                rawInstance.addToChilds(rawChild)
            }
        }



        return new RawParseResult<RawPregnancyOutcome>(rawInstance, errors)

    }

    RawParseResult<RawDeath> parseDeath(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = [:] //xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def paramsRelationships = new ArrayList<LinkedHashMap>()
        def rootnode = xmlNode?.name()

        xmlNode.childNodes().each { Node node ->

            if (node.childNodes().size()>0 && node.name().equalsIgnoreCase("newRelationships")){
                //println "children ${node.name()}"

                node.childNodes().eachWithIndex { Node child, index ->

                    if (child.name().equalsIgnoreCase("rawDeathRelationship")){
                        def cparams = [:]
                        child.childNodes().each { innernode ->
                            cparams.put(innernode.name(), innernode.text())
                        }

                        paramsRelationships.add(cparams)
                    }

                }
            } else {
                params.put(node.name(), node.text())
            }
        }

        if (!rootnode.equalsIgnoreCase("RawDeath")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawDeath>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.deathDate.size() > 0) {
            params.deathDate = StringUtil.toLocalDate(xmlNode.deathDate.text())

            if (params.deathDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.deathDate.text(), "deathDate"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawDeath = new RawDeath(params)
        rawDeath.id = params.id

        if (paramsRelationships.size() > 0) {
            def ordinal = 1
            paramsRelationships.each { cparams ->
                def rawRelationship = new RawDeathRelationship(cparams)
                rawRelationship.death = rawDeath
                rawRelationship.ordinal = ordinal++
                rawDeath.addToRelationships(rawRelationship)
            }
        }

        return new RawParseResult<RawDeath>(rawDeath, errors)

    }

    RawParseResult<RawChangeHead> parseChangeHead(NodeChild xmlNode) {

        //has a especial handler

        def errors = new ArrayList<RawMessage>()
        def params = [:]
        def paramsRelationships = new ArrayList<LinkedHashMap>()
        def rootnode = xmlNode?.name()

        xmlNode.childNodes().each { Node node ->

            if (node.childNodes().size()>0 && node.name().equalsIgnoreCase("relationships")){
                //println "children ${node.name()}"

                node.childNodes().eachWithIndex { Node child, index ->

                    if (child.name().equalsIgnoreCase("RawChangeHeadRelationship")){
                        def cparams = [:]
                        child.childNodes().each { innernode ->
                            cparams.put(innernode.name(), innernode.text())
                        }

                        paramsRelationships.add(cparams)
                    }

                }
            } else {
                params.put(node.name(), node.text())
            }
        }

        if (!rootnode.equalsIgnoreCase("RawChangeHead")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawChangeHead>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode?.eventDate.size() > 0) {
            params.eventDate = StringUtil.toLocalDate(xmlNode.eventDate.text())

            if (params.eventDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.eventDate.text(), "eventDate"])
            }
        }

        if (xmlNode?.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode?.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawChangeHead(params)
        rawInstance.id = params.id

        if (paramsRelationships.size() > 0) {
            def ordinal = 1
            paramsRelationships.each { cparams ->
                def rawRelationship = new RawChangeHeadRelationship(cparams)
                rawRelationship.changeHead = rawInstance
                rawRelationship.ordinal = ordinal++
                rawInstance.addToRelationships(rawRelationship)
            }
        }

        return new RawParseResult<RawChangeHead>(rawInstance, errors)

    }

    RawParseResult<RawIncompleteVisit> parseIncompleteVisit(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawIncompleteVisit")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawIncompleteVisit>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */


        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawIncompleteVisit = new RawIncompleteVisit(params)
        rawIncompleteVisit.id = params.id

        return new RawParseResult<RawIncompleteVisit>(rawIncompleteVisit, errors)

    }

    RawParseResult<RawChangeRegionHead> parseChangeRegionHead(NodeChild xmlNode) {

        //has a especial handler

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()


        if (!rootnode.equalsIgnoreCase("RawChangeRegionHead")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawChangeRegionHead>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode?.eventDate.size() > 0) {
            params.eventDate = StringUtil.toLocalDate(xmlNode.eventDate.text())

            if (params.eventDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.eventDate.text(), "eventDate"])
            }
        }

        if (xmlNode?.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode?.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawChangeRegionHead(params)
        rawInstance.id = params.id

        return new RawParseResult<RawChangeRegionHead>(rawInstance, errors)

    }

    RawParseResult<RawEditRegion> parseEditRegion(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("rawEditRegion")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawRegion>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */
        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawEditRegion(params)
        rawInstance.id = GeneralUtil.generateUUID()

        return new RawParseResult<RawEditRegion>(rawInstance, errors)

    }

    RawParseResult<RawEditHousehold> parseEditHousehold(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("rawEditHousehold")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawHousehold>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */
        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawEditHousehold(params)
        rawInstance.id = GeneralUtil.generateUUID()

        return new RawParseResult<RawEditHousehold>(rawInstance, errors)

    }

    RawParseResult<RawEditMember> parseEditMember(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("rawEditMember")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawMember>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.dob.size() > 0) {
            params.dob = StringUtil.toLocalDate(xmlNode.dob.text())

            if (params.dob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.dob.text(), "dob"])
            }
        }

        if (xmlNode.collectedDate.size() > 0) {
            params.collectedDate = StringUtil.toLocalDateTimePrecise(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate.size() > 0) {
            params.uploadedDate = StringUtil.toLocalDateTimePrecise(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        } else {
            params.uploadedDate = LocalDateTime.now()
        }

        /* start and end variables */
        if (xmlNode.start.size() > 0) {
            params.collectedStart = StringUtil.toLocalDateTimePrecise(xmlNode.start.text())

            if (params.collectedStart==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.start.text(), "collectedStart"])
            }
        }

        if (xmlNode.end.size() > 0) {
            params.collectedEnd = StringUtil.toLocalDateTimePrecise(xmlNode.end.text())

            if (params.collectedEnd==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.end.text(), "collectedEnd"])
            }
        }

        def rawInstance = new RawEditMember(params)
        rawInstance.id = GeneralUtil.generateUUID()

        return new RawParseResult<RawEditMember>(rawInstance, errors)

    }

    String getExtensionXmlText(String xmlContent, RawEntity rawEntity) {
        //contains main tag + <data> tag + <rawDomain> tag + <extension> tag
        //replace main tag
        xmlContent = xmlContent.replaceAll("<\\?xml[^\\?]*\\?>", "")
        //replace data tag
        xmlContent = xmlContent.replace("<xdata>", "")?.replaceAll("</xdata>", "")

        //replace extension tag
        def regex = "<${rawEntity.tag}>.*?/${rawEntity.tag}>"
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        // Use a Matcher to find and replace the matched pattern
        Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            xmlContent = matcher.replaceFirst("");
        }
        return xmlContent?.trim()
    }
}
