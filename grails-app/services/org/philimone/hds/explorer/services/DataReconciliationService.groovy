package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import org.hibernate.SessionFactory
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.ValidatableEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PartiallyDisabled
import org.philimone.hds.explorer.server.model.main.Residency

import java.time.LocalDateTime

@Transactional
class DataReconciliationService {

    def static finalStatuses = [HouseholdStatus.HOUSE_NOT_FOUND, HouseholdStatus.HOUSE_DESTROYED, HouseholdStatus.HOUSE_ABANDONED, HouseholdStatus.OTHER]

    SessionFactory sessionFactory

    def maritalRelationshipService
    def headRelationshipService
    def residencyService
    def generalUtilitiesService

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
        def households_ids = Household.executeQuery("select h.id from Household h")

        def processed = 0
        println "proccessing ${households_ids.size()} records"
        households_ids.collate(1000).each { batch ->
            def households = Household.findAllByIdInList(batch)

            households.each {household ->
                processed++

                //get residencies to determine household status and update members
                def residencies = Residency.countByHouseholdAndEndType(household, ResidencyEndType.NOT_APPLICABLE)
                def head = HeadRelationship.findByHouseholdAndRelationshipTypeAndEndType(household, HeadRelationshipType.HEAD_OF_HOUSEHOLD, HeadRelationshipEndType.NOT_APPLICABLE, [sort: "startDate", order: "desc"])

                //UPDATE HOUSEHOLD STATUS
                household.status = residencies > 0 ? HouseholdStatus.HOUSE_OCCUPIED : !finalStatuses.contains(household.status) ? HouseholdStatus.HOUSE_VACANT : household.status

                //UPDATE HEAD OF HOUSEHOLD
                if (head != null) {
                    household.headMember = head.member
                    household.headCode = head.member.code
                    household.headName = head.member.name
                } else {
                    household.headMember = null
                    household.headCode = null
                    household.headName = null
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

    def householdStatusReconcialiate(Household household) {
        //get residencies to determine household status and update members
        def residencies = Residency.countByHouseholdAndEndType(household, ResidencyEndType.NOT_APPLICABLE)
        //def head = HeadRelationship.findByHouseholdAndRelationshipTypeAndEndType(household, HeadRelationshipType.HEAD_OF_HOUSEHOLD, HeadRelationshipEndType.NOT_APPLICABLE, [sort: "startDate", order: "desc"])
        def headr = HeadRelationship.executeQuery("select h.member, h.member.code, h.member.name from HeadRelationship h where h.household=?0 and h.relationshipType=?1 and h.endType=?2 order by h.startDate desc", [household, HeadRelationshipType.HEAD_OF_HOUSEHOLD, HeadRelationshipEndType.NOT_APPLICABLE], [max: 1, offset: 0])
        def head = headr?.first()

        //UPDATE HOUSEHOLD STATUS
        household.status = residencies > 0 ? HouseholdStatus.HOUSE_OCCUPIED : !finalStatuses.contains(household.status) ? HouseholdStatus.HOUSE_VACANT : household.status

        //UPDATE HEAD OF HOUSEHOLD
        if (headr?.first() != null) {
            household.headMember = head[0]
            household.headCode = head[1]
            household.headName = head[2]
        } else {
            household.headMember = null
            household.headCode = null
            household.headName = null
        }

        //household.save()
        Household.executeUpdate("update Household h set h.status=?0, h.headMember=?1, h.headCode=?2, h.headName=?3 where h.id=?4", [household.status, household.headMember, household.headCode, household.headName, household.id])
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

                member.entryHousehold = null
                member.entryType = null
                member.entryDate = null
                member.startType = null
                member.startDate = null
                member.endType = null
                member.endDate = null
                member.headRelationshipType = HeadRelationshipType.DONT_KNOW

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
                        member.headRelationshipType = h != null && h.endType == HeadRelationshipEndType.NOT_APPLICABLE ? h.relationshipType : HeadRelationshipType.DONT_KNOW
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

    def restoreTemporarilyDisabledResidenciesHeadRelationships(String logReportFileId, LogOutput log) {

        def reportFile = LogReportFile.get(logReportFileId)
        def member_ids = new ArrayList<String>()

        //1. Restore partially invalidated records order by endDate
        //   - Restore on the most recent event record - the idea of disabling events is to allow other events to be inserted and then later restore them
        //2. Restore Invalidated records
        //3. Reconcialiation of Member Status


        //1. Restore partially invalidated records order by endDate
        def partialRecords = PartiallyDisabled.executeQuery("select p from PartiallyDisabled p where p.enabled=true order by p.memberCode, p.endDate asc")
;;;
        //backup affected member ids
        member_ids.addAll partialRecords.collect { it.member.id }
        member_ids.addAll Residency.executeQuery("select r.member.id from Residency r where r.status = ?0", [ValidatableStatus.TEMPORARILY_INACTIVE])
        member_ids.addAll HeadRelationship.executeQuery("select r.member.id from HeadRelationship r where r.status = ?0", [ValidatableStatus.TEMPORARILY_INACTIVE])

        println("restoring partially records ${partialRecords.size()}")
        //1.1 restore endType and endDate
        partialRecords.each { pRec ->
            if (pRec.entity == ValidatableEntity.HEAD_RELATIONSHIP) {
                //get current head relationship
                def currentHr = headRelationshipService.getLastHeadRelationship(pRec.member)

                if (currentHr.endType != HeadRelationshipEndType.NOT_APPLICABLE) {
                    def msg = generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partially.cant.restore.label", new Object[] {pRec.id, "HeadRelationship", currentHr.id}, "")
                    log.output.println(msg)

                    //disable partiallyDisabled record due to impossibility of restore, it must be NA
                    pRec.enabled = false
                    pRec.save(flush: true)

                    //activate the head relationship status
                    currentHr.status = ValidatableStatus.ACTIVE
                    currentHr.save(flush: true)

                    return //jump to next
                }

                //restore the event
                currentHr.endType = pRec.endType.getHeadRelationshipEndType()
                currentHr.endDate = pRec.endDate
                currentHr.status = ValidatableStatus.ACTIVE
                currentHr.save(flush: true)
                //delete the partial record
                pRec.delete(flush: true)

            } else if (pRec.entity == ValidatableEntity.RESIDENCY) {
                //get current residency
                def currentRes = residencyService.getCurrentResidency(pRec.member)

                if (currentRes.endType != ResidencyEndType.NOT_APPLICABLE) {
                    def msg = generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partially.cant.restore.label", new Object[] {pRec.id, "Residency", currentRes.id}, "")
                    log.output.println(msg)

                    //disable partiallyDisabled record due to impossibility of restore, it must be NA
                    pRec.enabled = false
                    pRec.save(flush: true)

                    //activate the residency status
                    currentRes.status = ValidatableStatus.ACTIVE
                    currentRes.save(flush: true)

                    return //jump to next
                }

                //restore the event
                currentRes.endType = pRec.endType.getResidencyEndType()
                currentRes.endDate = pRec.endDate
                currentRes.status = ValidatableStatus.ACTIVE
                currentRes.save(flush: true)
                //delete the partial record
                pRec.delete(flush: true)

            } else if (pRec.entity == ValidatableEntity.MARITAL_RELATIONSHIP) {
                //get current head relationship
                def currentMr = maritalRelationshipService.getCurrentMaritalRelationship(pRec.member, pRec.spouse)

                if (currentMr.endStatus != MaritalEndStatus.NOT_APPLICABLE) {
                    def msg = generalUtilitiesService.getMessage("rawDomain.helpers.temporarily.disabled.partially.cant.restore.label", new Object[] {pRec.id, "MaritalRelationship", currentMr.id}, "")
                    log.output.println(msg)

                    //disable partiallyDisabled record due to impossibility of restore, it must be NA
                    pRec.enabled = false
                    pRec.save(flush: true)

                    //activate the residency status
                    currentMr.status = ValidatableStatus.ACTIVE
                    currentMr.save(flush: true)

                    return //jump to next
                }

                //restore the event
                currentMr.endStatus = pRec.endType.getMaritalEndStatus()
                currentMr.endDate = pRec.endDate
                currentMr.status = ValidatableStatus.ACTIVE
                currentMr.save(flush: true)
                //delete the partial record
                pRec.delete(flush: true)
            }
        }

        println "restoring fully invalidated records of residency and headrelationship"
        //2. Restore Invalidated records
        Residency.executeUpdate("update Residency r set r.status = ?0 where r.status = ?1", [ValidatableStatus.ACTIVE, ValidatableStatus.TEMPORARILY_INACTIVE])
        HeadRelationship.executeUpdate("update HeadRelationship r set r.status = ?0 where r.status = ?1", [ValidatableStatus.ACTIVE, ValidatableStatus.TEMPORARILY_INACTIVE])
        MaritalRelationship.executeUpdate("update MaritalRelationship r set r.status = ?0 where r.status = ?1", [ValidatableStatus.ACTIVE, ValidatableStatus.TEMPORARILY_INACTIVE])


        println "reconcile all affected members ${member_ids.size()}"

        //3. Reconcile member status of affected members
        memberStatusReconcialiationBy(member_ids)

    }

    def memberStatusReconcialiationBy(List<String> members_ids) {
        //get all members
        //-> update Household Status, Household Head
        //def members_ids = Member.executeQuery("select m.id from Member m where m.code != ?0", ["UNK"])

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
