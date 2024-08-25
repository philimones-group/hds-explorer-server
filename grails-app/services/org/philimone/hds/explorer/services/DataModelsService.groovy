package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import grails.converters.JSON
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.enums.IncompleteVisitReason
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.MemberStatus
import org.philimone.hds.explorer.server.model.enums.NoVisitReason
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.VisitReason
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.PartiallyDisabledEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType


@Transactional
class DataModelsService {

    def generalUtilitiesService

    static final COLUMN_START_TYPE = "startType"
    static final COLUMN_START_DATE = "startDate"
    static final COLUMN_END_TYPE = "endType"
    static final COLUMN_END_DATE = "endDate"

    static final COLUMN_HEAD_RELATIONSHIP_TYPE = "headRelationshipType"

    static final COLUMN_START_STATUS = "startStatus"
    static final COLUMN_END_STATUS = "endStatus"
    static final COLUMN_IS_POLYGAMIC = "isPolygamic"




    def static defaultEnumTypes = [
            "ResidencyStartType" : ResidencyStartType.values(),
            "ResidencyEndType"   : ResidencyEndType.values(),
            "HeadRelationshipStartType" : HeadRelationshipStartType.values(),
            "HeadRelationshipEndType"   : HeadRelationshipEndType.values(),
            "HeadRelationshipType" : HeadRelationshipType.values(),
            "MaritalStartStatus" : MaritalStartStatus.values(),
            "MaritalEndStatus" : MaritalEndStatus.values(),
            "MaritalStatus" : MaritalStatus.values(),
            "PregnancyOutcomeType" : PregnancyOutcomeType.values(),
            "PartiallyDisabledEndType" : PartiallyDisabledEndType.values(),
            "InMigrationType" : InMigrationType.values(),
            "OutMigrationType" : OutMigrationType.values(),
            "ExternalInMigrationType" : ExternalInMigrationType.values(),
            "EstimatedDateOfDeliveryType": EstimatedDateOfDeliveryType.values(),
            "Gender" : Gender.values(),
            "HouseholdStatus" : HouseholdStatus.values(),
            "IncompleteVisitReason" : IncompleteVisitReason.values(),
            "MemberStatus" : MemberStatus.values(),
            "NoVisitReason" : NoVisitReason.values(),
            "PregnancyOutcomeType" : PregnancyOutcomeType.values(),
            "PregnancyStatus" : PregnancyStatus.values(),
            "ProcessedStatus" : ProcessedStatus.values(),
            "RegionLevel" : RegionLevel.values(),
            "VisitReason" : VisitReason.values(),
            "VisitLocationItem" : VisitLocationItem.values(),
            "BirthPlace" : BirthPlace.values()

    ] as HashMap<String, Enum[]>

    def static shortCodesEnumTypes = [
            "ResidencyStartType", "ResidencyEndType", "HeadRelationshipStartType", "HeadRelationshipEndType", "HeadRelationshipType",
            "MaritalStartStatus", "MaritalEndStatus", "MaritalStatus", "PregnancyOutcomeType", "PartiallyDisabledEndType", "InMigrationType", "OutMigrationType"
    ]

    boolean isRegisteredEnumType(String type) {
        return defaultEnumTypes.containsKey(type)
    }

    def getEnumValuesJSON(String type) {
        def values = defaultEnumTypes.get(type)

        def objects = [:]

        if (shortCodesEnumTypes.contains(type)) {
            objects = values.collectEntries {
                def prefix = it?.code ? "${it?.code} - " : ""
                [("${it?.code}"): prefix + message("${it?.name}")]
            }
        } else {

            objects = values.collectEntries {
                def code = it?.code==null ? "${it?.name()}" : "${it?.code}"
                def name = it?.name==null ? "${it?.name()}" : "${it?.name}"

                [(code): message(name)]
            }
        }


        return objects as JSON
    }

    def getEnumValuesArray(String type) {
        def objects = []
        def values = defaultEnumTypes.get(type)

        if (shortCodesEnumTypes.contains(type)) {
            objects = values.collect {
                def prefix = it?.code ? "${it?.code} - " : ""
                new EnumValue( id: "${it?.code}", name: (prefix + message("${it?.name}")))
            }
        } else {
            objects = values.collect {
                def code = it?.code==null ? "${it?.name()}" : "${it?.code}"
                def name = it?.name==null ? "${it?.name()}" : "${it?.name}"

                new EnumValue(id: code, name: message(name))
            }
        }

        return objects
    }

    List<RawEntity> findRawEntitiesLike(String text) {
        def list = []

        if (text==null) return RawEntity.values().toList()

        RawEntity.values().each { rawEntity ->
            def msg = message(rawEntity.name)
            if (msg?.toLowerCase()?.contains(text?.toLowerCase())){
                list.add(rawEntity)
            }
        }
        return list
    }

    private String message(String code) {
        generalUtilitiesService.getMessage(code, code)
    }

    class EnumValue {
        String id
        String name
    }
}
