package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.settings.Codes

@Transactional
class EventSyncRawDomainService {

    def generalUtilitiesService
    def eventSyncService
    def dataModelsService
    def grailsLinkGenerator

    def getRawDomainData(RawEventType rawEventType, params) {
        switch (rawEventType) {
            case RawEventType.EVENT_REGION:                       return getRegionData(params)
            case RawEventType.EVENT_HOUSEHOLD:                    return getHouseholdData(params)
            case RawEventType.EVENT_VISIT:                        return getVisitData(params)
            case RawEventType.EVENT_MEMBER_ENU:                   return getMemberEnuData(params)
            case RawEventType.EVENT_DEATH:                        return getDeathData(params)
            case RawEventType.EVENT_OUTMIGRATION:                 return getOutMigrationData(params)
            case RawEventType.EVENT_INTERNAL_INMIGRATION:         return getInternalInMigrationData(params)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY:   return getExternalInMigrationEntryData(params)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY: return getExternalInMigrationReentryData(params)
            case RawEventType.EVENT_PREGNANCY_REGISTRATION:       return getPregnancyRegistrationData(params)
            case RawEventType.EVENT_PREGNANCY_OUTCOME:            return getPregnancyOutcomeData(params)
            case RawEventType.EVENT_PREGNANCY_VISIT:              return getPregnancyVisitData(params)
            case RawEventType.EVENT_MARITAL_RELATIONSHIP:         return getMaritalRelationshipData(params)
            case RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD:     return getChangeHeadOfHouseholdData(params)
            case RawEventType.EVENT_INCOMPLETE_VISIT:             return getIncompleteVisitData(params)
            case RawEventType.EVENT_CHANGE_HEAD_OF_REGION:        return getChangeHeadOfRegionData(params)
            case RawEventType.EVENT_HOUSEHOLD_RELOCATION:         return getHouseholdRelocationData(params)
            case RawEventType.EVENT_CHANGE_PROXY_HEAD:            return getChangeProxyHeadData(params)
        }
    }

    def getData(){

    }

    def getRegionData(params){
        def svc = generalUtilitiesService
        def dateUtil = generalUtilitiesService.getDateUtil()

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

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] } //v.dir is asc or desc

        //event, uuid, column, code, creationDate, errorMessage

        //println(params)
        //println()
        //println "errorLog file $logReportFileInstance"
        //println()
        //println "errorLog orderList $orderList"


        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def date_search_filter = StringUtil.isBlank(search_filter) ? null : search_filter.replace(":", "%")
        def formattedEthDate = Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR ? eventSyncService.toGregorianSearchDate(params_search) : null //gets the possible date range

        if (formattedEthDate != null) {
            date_search_filter = formattedEthDate.replace(":", "%") + "%"
        }

        def filterer = {
            or {
                eq('processedStatus', ProcessedStatus.NOT_PROCESSED)
                eq('processedStatus', ProcessedStatus.ERROR)
            }
            or {
                if (search_filter) ilike 'id', search_filter
                if (search_filter) ilike 'regionCode', search_filter
                if (search_filter) ilike 'regionName', search_filter
                if (date_search_filter) sqlRestriction("DATE_FORMAT(collected_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                if (date_search_filter) sqlRestriction("DATE_FORMAT(uploaded_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                //if (search_filter) ilike 'message', search_filter
            }
            //def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])
        }

        //ORDERS

        def orderer = RawRegion.withCriteria {
            filterer.delegate = delegate
            filterer()
            if (orderList.empty) order 'id', 'asc'
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'id':    order 'id', oi[1]; break
                    //case 'event': order 'entity', oi[1]; break
                    //case 'code':  order 'code', oi[1]; break
                    case 'collectedDate': order 'collectedDate', oi[1]; break
                    case 'uploadedDate':  order 'uploadedDate', oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }

        //Display records
        def rawRecords = orderer.collect { rawObj ->
            def rawError = RawErrorLog.findByUuid(rawObj.id)
            ['event':         "<a href='${grailsLinkGenerator.link(controller: 'rawDomain', action: 'editRegion', id: rawObj.id)}'>${svc.getMessage(RawEventType.EVENT_REGION.name)}</a>",
             'uuid':          rawObj.id,
             'description':   rawObj.regionCode + " - "+rawObj.regionName,
             'collectedDate': dateUtil.formatYMDHMS(rawObj.collectedDate),
             'uploadedDate':  dateUtil.formatYMDHMS(rawObj.uploadedDate),
             'processed':     svc.getMessage(rawObj.processedStatus?.name),
             'errorMessage': "<td style=\"word-wrap: break-word;\">${rawError?.collapsedMessage==null ? "" : rawError?.collapsedMessage}</td>"
            ]
        }

        //FINAL PARAMETERS
        def recordsTotal = RawRegion.count()
        def rawObjCriteria = RawRegion.createCriteria()
        def recordsFiltered = rawObjCriteria.count {
            filterer.delegate = delegate
            filterer()
        }

        //println "rawObj recordsTotal $recordsTotal"
        //println "rawObj recordsFiltered $recordsFiltered"
        //println "rawObj ${errorLogs.size()}"

        [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: rawRecords]
    }

    def getHouseholdData(params){
        def svc = generalUtilitiesService
        def dateUtil = generalUtilitiesService.getDateUtil()

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

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] } //v.dir is asc or desc

        //event, uuid, column, code, creationDate, errorMessage

        //println(params)
        //println()
        //println "errorLog file $logReportFileInstance"
        //println()
        //println "errorLog orderList $orderList"


        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def date_search_filter = StringUtil.isBlank(search_filter) ? null : search_filter.replace(":", "%")
        def formattedEthDate = Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR ? eventSyncService.toGregorianSearchDate(params_search) : null //gets the possible date range

        if (formattedEthDate != null) {
            date_search_filter = formattedEthDate.replace(":", "%") + "%"
        }

        def filterer = {
            or {
                eq('processedStatus', ProcessedStatus.NOT_PROCESSED)
                eq('processedStatus', ProcessedStatus.ERROR)
            }
            or {
                if (search_filter) ilike 'id', search_filter
                if (search_filter) ilike 'householdCode', search_filter
                if (search_filter) ilike 'householdName', search_filter
                if (date_search_filter) sqlRestriction("DATE_FORMAT(collected_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                if (date_search_filter) sqlRestriction("DATE_FORMAT(uploaded_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                //if (search_filter) ilike 'message', search_filter
            }
            //def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])
        }

        //ORDERS

        def orderer = RawHousehold.withCriteria {
            filterer.delegate = delegate
            filterer()
            if (orderList.empty) order 'id', 'asc'
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'id':    order 'id', oi[1]; break
                        //case 'event': order 'entity', oi[1]; break
                        //case 'code':  order 'code', oi[1]; break
                    case 'collectedDate': order 'collectedDate', oi[1]; break
                    case 'uploadedDate':  order 'uploadedDate', oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }

        //Display records
        def rawRecords = orderer.collect { rawObj ->
            def rawError = RawErrorLog.findByUuid(rawObj.id)
            ['event':         "<a href='${grailsLinkGenerator.link(controller: 'rawDomain', action: 'editHousehold', id: rawObj.id)}'>${svc.getMessage(RawEventType.EVENT_HOUSEHOLD.name)}</a>",
             'uuid':          rawObj.id,
             'description':   rawObj.householdCode + " - "+rawObj.householdName,
             'collectedDate': dateUtil.formatYMDHMS(rawObj.collectedDate),
             'uploadedDate':  dateUtil.formatYMDHMS(rawObj.uploadedDate),
             'processed':     svc.getMessage(rawObj.processedStatus?.name),
             'errorMessage': "<td style=\"word-wrap: break-word;\">${rawError?.collapsedMessage==null ? "" : rawError?.collapsedMessage}</td>"
            ]
        }

        //FINAL PARAMETERS
        def recordsTotal = RawHousehold.count()
        def rawObjCriteria = RawHousehold.createCriteria()
        def recordsFiltered = rawObjCriteria.count {
            filterer.delegate = delegate
            filterer()
        }

        //println "rawObj recordsTotal $recordsTotal"
        //println "rawObj recordsFiltered $recordsFiltered"
        //println "rawObj ${errorLogs.size()}"

        [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: rawRecords]
    }

    def getVisitData(params) {
        getRawTableData(
                RawVisit,
                params,
                RawEventType.EVENT_VISIT,
                'editVisit',
                ['code','householdCode','respondentCode','respondentName','visitReason','visitLocation'],
                { r -> "${r.code}" }
        )
    }

    def getMemberEnuData(params) {
        getRawTableData(
                RawMemberEnu,
                params,
                RawEventType.EVENT_MEMBER_ENU,
                'editMemberEnu',
                ['code','name','gender','motherCode','fatherCode','householdCode','headRelationshipType'],
                { r -> "${r.code} - ${r.name}" }
        )
    }

    def getDeathData(params) {
        getRawTableData(
                RawDeath,
                params,
                RawEventType.EVENT_DEATH,
                'editDeath',
                ['memberCode','deathCause','deathPlace','visitCode'],
                { r -> "${r.memberCode}  •  ${r.deathDate}" }
        )
    }

    def getOutMigrationData(params) {
        getRawTableData(
                RawOutMigration,
                params,
                RawEventType.EVENT_OUTMIGRATION,
                'editOutMigration',
                ['memberCode','migrationType','originCode','destinationCode','destinationOther','visitCode'],
                { r -> "${r.memberCode}  •  ${r.originCode}" }
        )
    }

    def getInternalInMigrationData(params) {
        getRawTableData(
                RawInMigration,
                params,
                RawEventType.EVENT_INTERNAL_INMIGRATION,
                'editInMigration',
                ['memberCode','migrationType','originCode','destinationCode','headRelationshipType','visitCode'],
                { r -> "${r.memberCode}  •  ${r.originCode}" }
        )
    }

    def getExternalInMigrationEntryData(params) {
        getRawTableData(
                RawExternalInMigration,
                params,
                RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY,
                'editExternalInMigration',
                ['memberCode','memberName','memberGender','originCode','destinationCode','extMigrationType','visitCode'],
                { r -> "${r.memberCode} - ${r.memberName}" }
        )
    }

    def getExternalInMigrationReentryData(params) {
        getRawTableData(
                RawExternalInMigration,
                params,
                RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY,
                'editExternalInMigration',
                ['memberCode','memberName','memberGender','originCode','destinationCode','extMigrationType','visitCode'],
                { r -> "${r.memberCode} - ${r.memberName}" }
        )
    }

    def getPregnancyRegistrationData(params) {
        getRawTableData(
                RawPregnancyRegistration,
                params,
                RawEventType.EVENT_PREGNANCY_REGISTRATION,
                'editPregnancyRegistration',
                ['code','motherCode','status','visitCode'],
                { r -> "${r.code}" }
        )
    }

    def getPregnancyOutcomeData(params) {
        getRawTableData(
                RawPregnancyOutcome,
                params,
                RawEventType.EVENT_PREGNANCY_OUTCOME,
                'editPregnancyOutcome',
                ['code','motherCode','fatherCode','birthPlace','visitCode'],
                { r -> "${r.code}" }
        )
    }

    def getPregnancyVisitData(params) {
        getRawTableData(
                RawPregnancyVisit,
                params,
                RawEventType.EVENT_PREGNANCY_VISIT,
                'editPregnancyVisit',
                ['code','motherCode','status','visitType','visitCode'],
                { r -> "${r.code}  •  ${r.visitType}" }
        )
    }

    def getMaritalRelationshipData(params) {
        getRawTableData(
                RawMaritalRelationship,
                params,
                RawEventType.EVENT_MARITAL_RELATIONSHIP,
                'editMaritalRelationship',
                ['memberA','memberB','startStatus','endStatus','polygamicId'],
                { r -> "${r.memberA}  ↔  ${r.memberB}  •  ${r.startStatus ?: ''}" }
        )
    }

    def getChangeHeadOfHouseholdData(params) {
        getRawTableData(
                RawChangeHead,
                params,
                RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD,
                'editChangeHead',
                ['householdCode','oldHeadCode','newHeadCode','reason','visitCode'],
                { r -> "${r.householdCode}  •  ${r.oldHeadCode}" }
        )
    }

    def getIncompleteVisitData(params) {
        getRawTableData(
                RawIncompleteVisit,
                params,
                RawEventType.EVENT_INCOMPLETE_VISIT,
                'editIncompleteVisit',
                ['visitCode','householdCode','memberCode','reason','reasonOther'],
                { r -> "${r.visitCode ?: ''}  •  ${r.memberCode}" }
        )
    }

    def getChangeHeadOfRegionData(params) {
        getRawTableData(
                RawChangeRegionHead,
                params,
                RawEventType.EVENT_CHANGE_HEAD_OF_REGION,
                'editChangeRegionHead',
                ['regionCode','oldHeadCode','newHeadCode','reason','visitCode'],
                { r -> "${r.regionCode}  •  ${r.oldHeadCode ?: '—'} → ${r.newHeadCode}" }
        )
    }

    def getHouseholdRelocationData(params) {
        getRawTableData(
                RawHouseholdRelocation,
                params,
                RawEventType.EVENT_HOUSEHOLD_RELOCATION,
                'editHouseholdRelocation',
                ['originCode','destinationCode','headCode','reason','reasonOther','visitCode'],
                { r -> "${r.originCode} → ${r.destinationCode}" }
        )
    }

    def getChangeProxyHeadData(params) {
        getRawTableData(
                RawHouseholdProxyHead,
                params,
                RawEventType.EVENT_CHANGE_PROXY_HEAD,
                'editHouseholdProxyHead',
                ['visitCode','householdCode','proxyHeadType','proxyHeadCode','proxyHeadName','proxyHeadRole','reason','reasonOther'],
                { r ->
                    def who = r.proxyHeadCode ?: r.proxyHeadName ?: ''
                    "${r.householdCode}  •  ${who} (${r.proxyHeadType})"
                }
        )
    }

    //Generic Datatables builder for RAW domains ---
    private Map getRawTableData(Class rawDomainClass, Map params, RawEventType eventType, String editAction, List<String> searchColumns, Closure<String> descriptionBuilder /* (raw) -> String*/) {
        def svc = generalUtilitiesService
        def dateUtil = generalUtilitiesService.getDateUtil()

        //Parse DataTables params (same logic used on getRegionData/getHousehold
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField)) table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def params_search = jqdtParams.search?.value
        def columnsList  = jqdtParams.columns?.collect { k, v -> v.data } ?: []
        def orderList    = jqdtParams.order?.collect   { k, v -> [columnsList[v.column as Integer], v.dir] } ?: []

        //Search filters (text + date, with optional Ethiopian calendar) ---
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def date_search_filter = StringUtil.isBlank(search_filter) ? null : search_filter.replace(":", "%")
        def formattedEthDate = Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR ? eventSyncService.toGregorianSearchDate(params_search) : null
        if (formattedEthDate != null) {
            date_search_filter = formattedEthDate.replace(":", "%") + "%"
        }

        def filterer = {
            or {
                eq('processedStatus', ProcessedStatus.NOT_PROCESSED)
                eq('processedStatus', ProcessedStatus.ERROR)
            }
            if (eventType == RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY) {
                eq('extMigrationType', ExternalInMigrationType.ENTRY.name())
            } else if (eventType == RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY) {
                eq('extMigrationType', ExternalInMigrationType.REENTRY.name())
            }
            or {
                // Common id / date filters
                if (search_filter) ilike 'id', search_filter
                if (date_search_filter) sqlRestriction("DATE_FORMAT(collected_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                if (date_search_filter) sqlRestriction("DATE_FORMAT(uploaded_date , '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")

                // Domain-specific text columns
                searchColumns?.each { col ->
                    if (search_filter) ilike col, search_filter
                }
            }
        }

        //ORDER + PAGE
        def rows = rawDomainClass.withCriteria {
            filterer.delegate = delegate
            filterer()
            if (orderList.empty) order 'collectedDate', 'desc'
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'id':             order 'id', oi[1]; break
                    case 'collectedDate':  order 'collectedDate', oi[1]; break
                    case 'uploadedDate':   order 'uploadedDate', oi[1]; break
                    default:
                        // If a requested column is part of searchColumns and exists on the class, allow ordering by it
                        if (oi[0] && searchColumns.contains(oi[0])) {
                            order oi[0], oi[1]
                        }
                        break
                }
            }
            maxResults ( (jqdtParams.length ?: "10") as Integer )
            firstResult( (jqdtParams.start  ?: "0" ) as Integer )
        }

        //Map to Datatables row objects
        def data = rows.collect { rawObj ->
            def rawError = RawErrorLog.findByUuid(rawObj.id)
            [
                    'event'        : "<a href='${grailsLinkGenerator.link(controller: 'rawDomain', action: editAction, id: rawObj.id)}'>${svc.getMessage(eventType.name)}</a>",
                    'uuid'         : rawObj.id,
                    'description'  : descriptionBuilder(rawObj) ?: '',
                    'collectedDate': dateUtil.formatYMDHMS(rawObj.collectedDate),
                    'uploadedDate' : dateUtil.formatYMDHMS(rawObj.uploadedDate),
                    'processed'    : svc.getMessage(rawObj.processedStatus?.name),
                    'errorMessage' : "<td style=\"word-wrap: break-word;\">${rawError?.collapsedMessage ?: ''}</td>"
            ]
        }

        // --- Totals ---
        def recordsTotal = rawDomainClass.count()
        def recordsFiltered = rawDomainClass.createCriteria().count {
            filterer.delegate = delegate
            filterer()
        }

        [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: data]
    }

}
