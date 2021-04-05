package org.philimone.hds.explorer.server.model.collect.raw.api

import grails.gorm.transactions.Transactional
import groovy.util.slurpersupport.NodeChild
import groovy.util.slurpersupport.Node
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.main.collect.raw.RawParseResult

@Transactional
class RawImportApiService {

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

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawRegion>(new RawRegion(params), errors)

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

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawHousehold>(new RawHousehold(params), errors)

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

        if (xmlNode.dob != null) {
            params.dob = StringUtil.toLocalDate(xmlNode.dob.text())

            if (params.dob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.dob.text(), "dob"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawMember>(new RawMember(params), errors)

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

        if (xmlNode.visitDate != null) {
            params.visitDate = StringUtil.toLocalDate(xmlNode.visitDate.text())

            if (params.visitDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.visitDate.text(), "visitDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawVisit>(new RawVisit(params), errors)

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

        if (xmlNode.dob != null) {
            params.dob = StringUtil.toLocalDate(xmlNode.dob.text())

            if (params.dob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.dob.text(), "dob"])
            }
        }

        if (xmlNode.residencyStartDate != null) {
            params.residencyStartDate = StringUtil.toLocalDate(xmlNode.residencyStartDate.text())

            if (params.residencyStartDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.residencyStartDate.text(), "residencyStartDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawMemberEnu>(new RawMemberEnu(params), errors)

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

        if (xmlNode.memberDob != null) {
            params.memberDob = StringUtil.toLocalDate(xmlNode.memberDob.text())

            if (params.memberDob==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.memberDob.text(), "memberDob"])
            }
        }

        if (xmlNode.migrationDate != null) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawExternalInMigration>(new RawExternalInMigration(params), errors)

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

        if (xmlNode.migrationDate != null) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawInMigration>(new RawInMigration(params), errors)

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

        if (xmlNode.migrationDate != null) {
            params.migrationDate = StringUtil.toLocalDate(xmlNode.migrationDate.text())

            if (params.migrationDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.migrationDate.text(), "migrationDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawOutMigration>(new RawOutMigration(params), errors)

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

        if (xmlNode.startDate != null) {
            params.startDate = StringUtil.toLocalDate(xmlNode.startDate.text())

            if (params.startDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.startDate.text(), "startDate"])
            }
        }

        if (xmlNode.endDate != null) {
            params.endDate = StringUtil.toLocalDate(xmlNode.endDate.text())

            if (params.endDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.endDate.text(), "endDate"])
            }
        }

        return new RawParseResult<RawHeadRelationship>(new RawHeadRelationship(params), errors)

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

        if (xmlNode.startDate != null) {
            params.startDate = StringUtil.toLocalDate(xmlNode.startDate.text())

            if (params.startDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.startDate.text(), "startDate"])
            }
        }

        if (xmlNode.endDate != null) {
            params.endDate = StringUtil.toLocalDate(xmlNode.endDate.text())

            if (params.endDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.endDate.text(), "endDate"])
            }
        }

        return new RawParseResult<RawMaritalRelationship>(new RawMaritalRelationship(params), errors)

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

        if (xmlNode.recordedDate != null) {
            params.recordedDate = StringUtil.toLocalDate(xmlNode.recordedDate.text())

            if (params.recordedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.recordedDate.text(), "recordedDate"])
            }
        }

        if (xmlNode.eddDate != null) {
            params.eddDate = StringUtil.toLocalDate(xmlNode.eddDate.text())

            if (params.eddDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.eddDate.text(), "eddDate"])
            }
        }

        if (xmlNode.lmpDate != null) {
            params.lmpDate = StringUtil.toLocalDate(xmlNode.lmpDate.text())

            if (params.lmpDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.lmpDate.text(), "lmpDate"])
            }
        }

        if (xmlNode.expectedDeliveryDate != null) {
            params.expectedDeliveryDate = StringUtil.toLocalDate(xmlNode.expectedDeliveryDate.text())

            if (params.expectedDeliveryDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.expectedDeliveryDate.text(), "expectedDeliveryDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawPregnancyRegistration>(new RawPregnancyRegistration(params), errors)

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

        if (xmlNode?.outcomeDate != null) {
            params.outcomeDate = StringUtil.toLocalDate(xmlNode.outcomeDate.text())

            if (params.outcomeDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.outcomeDate.text(), "outcomeDate"])
            }
        }

        if (xmlNode?.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode?.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        def rawInstance = new RawPregnancyOutcome(params)

        if (paramsChild.size() > 0) {
            paramsChild.each { cparams ->
                def rawChild = new RawPregnancyChild(cparams)
                rawChild.outcome = rawInstance
                rawInstance.addToChilds(rawChild)
            }
        }

        return new RawParseResult<RawPregnancyOutcome>(rawInstance, errors)

    }

    RawParseResult<RawPregnancyChild> parsePregnancyChild(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawPregnancyChild")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawPregnancyChild>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        return new RawParseResult<RawPregnancyChild>(new RawPregnancyChild(params), errors)

    }

    RawParseResult<RawDeath> parseDeath(NodeChild xmlNode) {

        def errors = new ArrayList<RawMessage>()
        def params = xmlNode.childNodes().collectEntries{[it.name(), it.text()]}
        def rootnode = xmlNode?.name()

        if (!rootnode.equalsIgnoreCase("RawDeath")) {
            errors << errorMessageService.getRawMessage("validation.field.raw.parsing.rootnode.invalid.error", [rootnode])
            return new RawParseResult<RawDeath>(null, errors)
        }

        /* converting non-primitive types must be parsed manually */

        if (xmlNode.deathDate != null) {
            params.deathDate = StringUtil.toLocalDate(xmlNode.deathDate.text())

            if (params.deathDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdate.error", [xmlNode?.deathDate.text(), "deathDate"])
            }
        }

        if (xmlNode.collectedDate != null) {
            params.collectedDate = StringUtil.toLocalDateTime(xmlNode.collectedDate.text())

            if (params.collectedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.collectedDate.text(), "collectedDate"])
            }
        }

        if (xmlNode.uploadedDate != null) {
            params.uploadedDate = StringUtil.toLocalDateTime(xmlNode.uploadedDate.text())

            if (params.uploadedDate==null) {
                errors << errorMessageService.getRawMessage("validation.field.raw.parsing.localdatetime.error", [xmlNode?.uploadedDate.text(), "uploadedDate"])
            }
        }

        return new RawParseResult<RawDeath>(new RawDeath(params), errors)

    }
}
