package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.odk.model.CensusHouseholdCore
import org.philimone.hds.explorer.odk.model.CensusMemberCore
import org.philimone.hds.explorer.server.model.json.SyncProcessedStatus

@Deprecated
@Transactional
class DssSynchronizationStatusService {

    def datasources = ['odk']

    List<SyncProcessedStatus> mainProcessedStatus() {
        
        def list = [
                getHouseholdCensusLocationStatus(),
                getHouseholdCensusSocialgroupStatus(),
                getMemberCensusIndividualStatus(),
                //getMemberCensusResidencyStatus(),
                getMemberCensusMembershipStatus(),
                getMemberCensusRelationshipStatus()
        ]

        return list
    }

    SyncProcessedStatus getHouseholdCensusLocationStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusHouseholdCore.withTransaction {
            //total records
            status.name = 'Census - Household Form -> Location'
            status.totalRecords = CensusHouseholdCore.count()
            status.processed = CensusHouseholdCore.countByLocationProcessed(1)
            status.processedWithError = CensusHouseholdCore.countByLocationProcessed(2)
            status.notProcessed = CensusHouseholdCore.countByLocationProcessed(0)
            status.invalidated = CensusHouseholdCore.countByLocationProcessed(4)
            status.otherCases = CensusHouseholdCore.countByLocationProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

    SyncProcessedStatus getHouseholdCensusSocialgroupStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusHouseholdCore.withTransaction {
            //total records
            status.name = 'Census - Household Form -> Socialgroup'
            status.totalRecords = CensusHouseholdCore.count()
            status.processed = CensusHouseholdCore.countBySocialgroupProcessed(1)
            status.processedWithError = CensusHouseholdCore.countBySocialgroupProcessed(2)
            status.notProcessed = CensusHouseholdCore.countBySocialgroupProcessed(0)
            status.invalidated = CensusHouseholdCore.countBySocialgroupProcessed(4)
            status.otherCases = CensusHouseholdCore.countBySocialgroupProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

    SyncProcessedStatus getMemberCensusIndividualStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusMemberCore.withTransaction {
            //total records
            status.name = 'Census - Member Form -> Individual'
            status.totalRecords = CensusMemberCore.count()
            status.processed = CensusMemberCore.countByIndividualProcessed(1)
            status.processedWithError = CensusMemberCore.countByIndividualProcessed(2)
            status.notProcessed = CensusMemberCore.countByIndividualProcessed(0)
            status.invalidated = CensusMemberCore.countByIndividualProcessed(4)
            status.otherCases = CensusMemberCore.countByIndividualProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

    SyncProcessedStatus getMemberCensusResidencyStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusMemberCore.withTransaction {
            //total records
            status.name = 'Census - Member Form -> Residency'
            status.totalRecords = CensusMemberCore.count()
            status.processed = CensusMemberCore.countByResidencyProcessed(1)
            status.processedWithError = CensusMemberCore.countByResidencyProcessed(2)
            status.notProcessed = CensusMemberCore.countByResidencyProcessed(0)
            status.invalidated = CensusMemberCore.countByResidencyProcessed(4)
            status.otherCases = CensusMemberCore.countByResidencyProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

    SyncProcessedStatus getMemberCensusMembershipStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusMemberCore.withTransaction {
            //total records
            status.name = 'Census - Member Form -> Membership'
            status.totalRecords = CensusMemberCore.count()
            status.processed = CensusMemberCore.countByMembershipProcessed(1)
            status.processedWithError = CensusMemberCore.countByMembershipProcessed(2)
            status.notProcessed = CensusMemberCore.countByMembershipProcessed(0)
            status.invalidated = CensusMemberCore.countByMembershipProcessed(4)
            status.otherCases = CensusMemberCore.countByMembershipProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

    SyncProcessedStatus getMemberCensusRelationshipStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        CensusMemberCore.withTransaction {
            //total records
            status.name = 'Census - Member Form -> Relationship (Aproxim.)'
            status.totalRecords = CensusMemberCore.count()
            status.processed = CensusMemberCore.countByRelationshipProcessed(1)
            status.processedWithError = CensusMemberCore.countByRelationshipProcessed(2)
            status.notProcessed = CensusMemberCore.countByRelationshipProcessed(0)
            status.invalidated = CensusMemberCore.countByRelationshipProcessed(4)
            status.otherCases = CensusMemberCore.countByRelationshipProcessedGreaterThan(4) //cases in analisys

        }

        return status
    }

}
