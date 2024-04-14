package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Residency

import java.time.LocalDateTime

@Transactional
class DataReconciliationService {

    SessionFactory sessionFactory

    def maritalRelationshipService

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        //System.gc()
        //println "clearing up"
    }

    def executeHouseholdStatusReconciliation(LogReportCode logReportId) {
        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            householdStatusReconcialiation()
        }catch (Exception ex){
            ex.printStackTrace()
            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)
            //println("errors: ${logReport.errors}")
        }

        println "finished household reconciliation"
    }

    def executeMemberStatusReconciliation(LogReportCode logReportId) {
        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            memberStatusReconcialiation()
        }catch (Exception ex){
            ex.printStackTrace()
            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)
            //println("errors: ${logReport.errors}")
        }

        println "finished member reconciliation"
    }

    def householdStatusReconcialiation() {
        //get all households
        //-> update Household Status, Household Head
        def finalStatuses = [HouseholdStatus.HOUSE_NOT_FOUND, HouseholdStatus.HOUSE_DESTROYED, HouseholdStatus.HOUSE_ABANDONED, HouseholdStatus.OTHER]
        def households_ids = Household.executeQuery("select h.id from Household h")

        def processed = 0
        println "proccessing ${households_ids.size()} records"
        households_ids.collate(1000).each { batch ->
            def households = Household.findAllByIdInList(batch)

            households.each {household ->
                processed++

                //get residencies to determine household status and update members
                def residencies = Residency.findAllByHouseholdAndEndType(household, ResidencyEndType.NOT_APPLICABLE, [sort: "startDate", order: "asc"])
                def relationships = HeadRelationship.findAllByHouseholdAndEndType(household, HeadRelationshipEndType.NOT_APPLICABLE, [sort: "startDate", order: "asc"])

                //UPDATE HOUSEHOLD STATUS
                if (residencies.size()==0) {
                    if (!finalStatuses.contains(household.status)) {
                        //maintain last status
                        household.status = HouseholdStatus.HOUSE_VACANT;
                    }
                } else {
                    household.status = HouseholdStatus.HOUSE_OCCUPIED
                }

                //UPDATE HEAD OF HOUSEHOLD
                def head = relationships.find { it.relationshipType== HeadRelationshipType.HEAD_OF_HOUSEHOLD}
                if (head != null) {
                    household.headMember = head.member
                    household.headCode = head.member.code
                    household.headName = head.member.name
                }

                //household.save()
                Household.executeUpdate("update Household h set h.status=?0, h.headMember=?1, h.headCode=?2, h.headName=?3 where h.id=?4", [household.status, household.headMember, household.headCode, household.headName, household.id])

                if (processed % 500 == 0) {
                    cleanUpGorm()
                    println "clearing ${processed}"
                }
            }
        }
    }

    def memberStatusReconcialiation() {
        //get all members
        //-> update Household Status, Household Head
        def members_ids = Member.executeQuery("select m.id from Member m where m.code != ?0", ["UNK"])

        def processed = 0
        members_ids.collate(1000).each { batch ->
            def members = Member.findAllByIdInList(batch)

            members.each { member ->
                processed++

                def firstRes = Residency.executeQuery("select r from Residency r where r.member=?0 order by r.startDate asc", [member], [max: 1])
                def lastRes = Residency.executeQuery("select r from Residency r where r.member=?0 order by r.startDate desc", [member], [max: 1])
                def maritalRelationship = maritalRelationshipService.getCurrentMaritalRelationship(member)

                if (!firstRes?.empty) {
                    def residency = firstRes.first() as Residency
                    member.entryHousehold = residency.householdCode
                    member.entryType = residency.startType
                    member.entryDate = residency.startDate
                }

                if (!lastRes?.empty) {
                    def residency = lastRes.first() as Residency
                    member.household = residency.household
                    member.householdCode = residency.householdCode
                    member.householdName = residency.household.name
                    member.startType = residency.startType
                    member.startDate = residency.startDate
                    member.endType = residency.endType
                    member.endDate = residency.endDate

                    def lastHeadRel = HeadRelationship.executeQuery("select h from HeadRelationship h where h.member=?0 and h.household=?1 order by h.startDate desc", [member, residency.household], [max: 1])

                    if (!lastHeadRel?.empty) {
                        def h = lastHeadRel.first() as HeadRelationship
                        member.headRelationshipType = h.relationshipType
                    } else {
                        member.headRelationshipType = HeadRelationshipType.DONT_KNOW
                    }
                }

                if (maritalRelationship != null) {
                    def spouse = maritalRelationship.memberA_code.equals(member.code) ? maritalRelationship.memberB : maritalRelationship.memberA
                    member.maritalStatus = maritalRelationshipService.getMaritalStatusFrom(maritalRelationship)

                    member.spouse = spouse
                    member.spouseCode = spouse.code
                    member.spouseName = spouse.name
                } else {
                    member.maritalStatus = MaritalStatus.SINGLE
                    member.spouse = null
                    member.spouseCode = null
                    member.spouseName = null
                }

                Member.executeUpdate("" +
                        "update Member m set m.entryHousehold=?0, m.entryType=?1, m.entryDate=?2," +
                        "                    m.household=?3, m.householdCode=?4, m.householdName=?5," +
                        "                    m.startType=?6, m.startDate=?7, m.endType=?8, m.endDate=?9," +
                        "                    m.headRelationshipType=?10," +
                        "                    m.maritalStatus=?11, m.spouse=?12, m.spouseCode=?13, m.spouseName=?14 " +
                        "where m.id=?15", [member.entryHousehold, member.entryType, member.entryDate,
                                           member.household, member.householdCode, member.householdName,
                                           member.startType, member.startDate, member.endType, member.endDate,
                                           member.headRelationshipType,
                                           member.maritalStatus, member.spouse, member.spouseCode, member.spouseName,
                                           member.id])


                //member.save()

                if (processed % 500 == 0) {
                    cleanUpGorm()
                    println "clearing ${processed}"
                }
            }
        }
    }
}
