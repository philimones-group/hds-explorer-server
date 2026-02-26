package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import net.betainteractive.io.writers.ZipMaker
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.*
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.*

import java.time.LocalDateTime

/**
 *  Responsible for generating XML/Zip Files to be exported - eg. Households, Members, Users...
 */
@Transactional
class SyncFilesOptimizedService {

    def sessionFactory

    def generalUtilitiesService
    def trackingListService
    def syncFilesReportService
    def moduleService
    def settingsService
    def residencyService
    def maritalRelationshipService

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()

        System.gc()
        //println "clearing up"
    }

    def generateMembersXML_Optimized_G1(LogReportCode logReportId) {
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "generate-members-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED
        int count = 0

        try {
            // 1. Fetch all Member IDs first to drive the batching
            println "Step 1/4: Reading Member IDs..."
            def memberIds = Member.executeQuery("select m.id from Member m")

            // 2. BULK FETCH RESIDENCIES: 1 query instead of 438,689
            println "Step 2/4: Bulk loading latest Residencies..."
            def latestResMap = new HashMap<String, Object[]>(450000)

            // We order by startDate ASC so the 'put' will eventually store the 'max' date for each member
            def resRows = Residency.executeQuery("select r.member.id, r.householdCode, r.household.name, r.startType, r.startDate, r.endType, r.endDate from Residency r where (r.status <> ?0 or r.status is null) order by r.startDate asc", [ValidatableStatus.TEMPORARILY_INACTIVE])

            resRows.each { row ->
                latestResMap.put(row[0], row)
            }
            resRows = null // Clear memory

            // 3. BULK FETCH MARITAL RELATIONSHIPS: 1 query instead of 877,000
            println "Step 3/4: Bulk loading Marital Relationships..."
            def maritalMap = new HashMap<String, Object[]>(450000)
            // Fetching raw data columns to keep memory footprint lean
            def mrRows = MaritalRelationship.executeQuery("select m.memberA.id, m.memberB.id, m.memberA.code, m.memberB.code, m.memberA.name, m.memberB.name, m.startDate, m.maritalStatus from MaritalRelationship m where (m.status <> ?0 or m.status is null) order by m.startDate asc", [ValidatableStatus.TEMPORARILY_INACTIVE])

            mrRows.each { row ->
                // row[0]=idA, row[1]=idB, row[2]=codeA, row[3]=codeB, row[4]=nameA, row[5]=nameB, row[6]=startDate, row[7]=maritalStatus
                // Mapping to both member IDs involved so either lookup works
                maritalMap.put(row[0], row)
                maritalMap.put(row[1], row)
            }
            mrRows = null // Clear memory

            // 4. GENERATE XML
            println "Step 4/4: Generating XML for ${memberIds.size()} records..."
            PrintStream outputFile = new PrintStream(new FileOutputStream(SystemPath.generatedFilesPath + "/members.xml"), true)

            outputFile.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><members>")

            memberIds.collate(1000).each { batch ->
                // Use readOnly to bypass Hibernate's dirty checking/session overhead
                def list = Member.withSession { session ->
                    Member.findAllByIdInList(batch, [readOnly: true])
                }

                list.each { m ->
                    count++
                    def resData = latestResMap.get(m.id)
                    def marData = maritalMap.get(m.id)

                    outputFile.print(toMemberXML_Optimized_G1(m, resData, marData))

                    if (count % 2000 == 0) {
                        cleanUpGorm()
                    }
                }
            }

            outputFile.print("</members>")
            outputFile.close()

            // Clean up Maps
            latestResMap = null
            maritalMap = null

            println("File saved! - members.xml");
            output.println("File saved! - members.xml");

            ZipMaker zipMaker = new ZipMaker(SystemPath.generatedFilesPath + "/members.zip")
            zipMaker.addFile(SystemPath.generatedFilesPath + "/members.xml")
            def b = zipMaker.makeZip()
            println "creating zip - members.zip - success=" + b

            processed = 1
            syncFilesReportService.update(SyncEntity.MEMBERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            output.println(ex.toString())
            logStatusValue = LogStatus.ERROR
        }

        // Logging remains exactly as your original code
        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }
        output.close();
    }

    private String toMemberXML_Optimized_G1(Member m, Object[] r, Object[] mr) {
        // Residency Extraction
        def householdCode = r ? r[1] : null
        def householdName = r ? r[2] : null
        def startType     = r ? r[3] : null
        def startDate     = r ? r[4] : null
        def endType       = r ? r[5] : null
        def endDate       = r ? r[6] : null

        // Marital Status Extraction (Handling memberA/memberB logic in-memory)
        def maritalStatus = null
        def spouseCode = null
        def spouseName = null

        if (mr) {
            maritalStatus = mr[7] // maritalStatus enum
            if (mr[2].equals(m.code)) { // If I am memberA, spouse is memberB
                spouseCode = mr[3]
                spouseName = mr[5]
            } else { // I am memberB, spouse is memberA
                spouseCode = mr[2]
                spouseName = mr[4]
            }
        }

        // StringBuilder is much faster for 438k iterations than String + String
        StringBuilder sb = new StringBuilder()
        sb.append("<member>")
        sb.append((m.code==null || m.code.isEmpty()) ? "<code />" : "<code>${m.code}</code>")
        sb.append((m.name==null || m.name.isEmpty()) ? "<name />" : "<name>${m.name}</name>")
        sb.append((m.gender==null ) ? "<gender />" : "<gender>${m.gender.code}</gender>")
        sb.append((m.dob==null) ? "<dob />" : "<dob>${StringUtil.format(m.dob)}</dob>")
        sb.append((m.age==null) ? "<age />" : "<age>${m.age}</age>")
        sb.append((m.ageAtDeath==null) ? "<ageAtDeath />" : "<ageAtDeath>${m.ageAtDeath}</ageAtDeath>")
        sb.append((m.motherCode==null || m.motherCode.isEmpty()) ? "<motherCode />" : "<motherCode>${m.motherCode}</motherCode>")
        sb.append((m.motherName==null || m.motherName.isEmpty()) ? "<motherName />" : "<motherName>${m.motherName}</motherName>")
        sb.append((m.fatherCode==null || m.fatherCode.isEmpty()) ? "<fatherCode />" : "<fatherCode>${m.fatherCode}</fatherCode>")
        sb.append((m.fatherName==null || m.fatherName.isEmpty()) ? "<fatherName />" : "<fatherName>${m.fatherName}</fatherName>")

        sb.append((maritalStatus==null) ? "<maritalStatus />" : "<maritalStatus>${maritalStatus.code}</maritalStatus>")
        sb.append((spouseCode==null || spouseCode.isEmpty()) ? "<spouseCode />" : "<spouseCode>${spouseCode}</spouseCode>")
        sb.append((spouseName==null || spouseName.isEmpty()) ? "<spouseName />" : "<spouseName>${spouseName}</spouseName>")

        sb.append((m.education==null || m.education.isEmpty()) ? "<education />" : "<education>${m.education}</education>")
        sb.append((m.religion==null || m.religion.isEmpty()) ? "<religion />" : "<religion>${m.religion}</religion>")
        sb.append((m.phonePrimary==null || m.phonePrimary.isEmpty()) ? "<phonePrimary />" : "<phonePrimary>${m.phonePrimary}</phonePrimary>")
        sb.append((m.phoneAlternative==null || m.phoneAlternative.isEmpty()) ? "<phoneAlternative />" : "<phoneAlternative>${m.phoneAlternative}</phoneAlternative>")

        sb.append((householdCode==null || householdCode?.isEmpty()) ? "<householdCode />" : "<householdCode>${householdCode}</householdCode>")
        sb.append((householdName==null || householdName?.isEmpty()) ? "<householdName />" : "<householdName>${householdName}</householdName>")

        sb.append((startType==null) ? "<startType />" : "<startType>${startType.code}</startType>")
        sb.append((startDate==null) ? "<startDate />" : "<startDate>${StringUtil.format(startDate)}</startDate>")
        sb.append((endType==null) ? "<endType />" : "<endType>${endType.code}</endType>")
        sb.append((endDate==null) ? "<endDate />" : "<endDate>${StringUtil.format(endDate)}</endDate>")

        sb.append((m.entryHousehold==null || m.entryHousehold.isEmpty()) ? "<entryHousehold />" : "<entryHousehold>${m.entryHousehold}</entryHousehold>")
        sb.append((m.entryType==null) ? "<entryType />" : "<entryType>${m.entryType.code}</entryType>")
        sb.append((m.entryDate==null) ? "<entryDate />" : "<entryDate>${StringUtil.format(m.entryDate)}</entryDate>")

        sb.append((m.headRelationshipType==null) ? "<headRelationshipType />" : "<headRelationshipType>${m.headRelationshipType.code}</headRelationshipType>")
        sb.append((m.headRelationshipType==null) ? "<isHouseholdHead />" : "<isHouseholdHead>${m.isHouseholdHead()}</isHouseholdHead>")

        sb.append((m.gpsAccuracy == null) ? "<gpsAccuracy />" : "<gpsAccuracy>${m.gpsAccuracy}</gpsAccuracy>")
        sb.append((m.gpsAltitude == null) ? "<gpsAltitude />" : "<gpsAltitude>${m.gpsAltitude}</gpsAltitude>")
        sb.append((m.gpsLatitude == null) ? "<gpsLatitude />" : "<gpsLatitude>${m.gpsLatitude}</gpsLatitude>")
        sb.append((m.gpsLongitude == null) ? "<gpsLongitude />" : "<gpsLongitude>${m.gpsLongitude}</gpsLongitude>")

        sb.append((m.collectedId == null) ? "<collectedId />" : "<collectedId>${m.collectedId}</collectedId>")
        sb.append((m.modules.empty) ? "<modules />" : "<modules>${moduleService.getListModulesAsText(m.modules)}</modules>")
        sb.append("</member>")

        return sb.toString()
    }

    def generateMembersXML_Optimized_C1(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.logsPath, "generate-members-xml");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {
            println "reading members"
            def memberIds = Member.executeQuery("select m.id from Member m")

            println "creating xml file ${memberIds.size()}"
            PrintStream outputFile = new PrintStream(new FileOutputStream(SystemPath.generatedFilesPath + "/members.xml"), true)
            outputFile.print("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><members>")

            int count = 0

            memberIds.collate(500).each { batch ->
                def members = Member.findAllByIdInList(batch)

                // --- Bulk-fetch current residency for the entire batch ---
                // Returns one row per member (the most recent non-inactive residency).
                // We use a subquery to pick the max startDate per member, then join back.
                def residencyRows = Residency.executeQuery("""
                select r.member.id, r.householdCode, r.household.name,
                       r.startType, r.startDate, r.endType, r.endDate
                from   Residency r
                where  r.member.id in (:ids)
                  and  (r.status <> :status or r.status is null)
                  and  r.startDate = (
                           select max(r2.startDate)
                           from   Residency r2
                           where  r2.member.id = r.member.id
                             and  (r2.status <> :status or r2.status is null)
                       )
            """, [ids: batch, status: ValidatableStatus.TEMPORARILY_INACTIVE])

                // Build a map: memberId -> residency row
                def residencyMap = residencyRows.collectEntries { row ->
                    [row[0], row]   // row[0] = member.id
                }

                // --- Bulk-fetch current marital relationships for the entire batch ---
                // Mirrors getCurrentMaritalRelationship(): fetches relationships where the member
                // appears as memberA OR memberB, excluding TEMPORARILY_INACTIVE, ordered by startDate desc.
                // We fetch all non-inactive relationships for the batch and replicate the
                // "pick the most recent between relationA and relationB" logic in Groovy.
                def maritalRowsA = MaritalRelationship.executeQuery("""
                select r from MaritalRelationship r
                where  r.memberA.id in (:ids)
                  and  (r.status <> :status or r.status is null)
                order by r.startDate desc
            """, [ids: batch, status: ValidatableStatus.TEMPORARILY_INACTIVE])

                def maritalRowsB = MaritalRelationship.executeQuery("""
                select r from MaritalRelationship r
                where  r.memberB.id in (:ids)
                  and  (r.status <> :status or r.status is null)
                order by r.startDate desc
            """, [ids: batch, status: ValidatableStatus.TEMPORARILY_INACTIVE])

                // Build maps: memberId -> most recent MaritalRelationship (as memberA / as memberB)
                // Since results are ordered by startDate desc, the first entry per member is the most recent.
                def maritalMapA = [:]
                maritalRowsA.each { mr ->
                    if (!maritalMapA.containsKey(mr.memberA.id)) {
                        maritalMapA[mr.memberA.id] = mr
                    }
                }
                def maritalMapB = [:]
                maritalRowsB.each { mr ->
                    if (!maritalMapB.containsKey(mr.memberB.id)) {
                        maritalMapB[mr.memberB.id] = mr
                    }
                }

                // Replicate the exact tie-breaking logic from getCurrentMaritalRelationship()
                def maritalMap = [:]
                (maritalMapA.keySet() + maritalMapB.keySet()).each { memberId ->
                    def relA = maritalMapA[memberId]
                    def relB = maritalMapB[memberId]
                    if      (relA == null) maritalMap[memberId] = relB
                    else if (relB == null) maritalMap[memberId] = relA
                    else maritalMap[memberId] = (relA.startDate > relB.startDate) ? relA : relB
                }

                // --- Build XML using pre-fetched maps (zero extra queries per member) ---
                members.each { m ->
                    count++
                    def resRow = residencyMap[m.id]
                    def mr     = maritalMap[m.id]
                    outputFile.print(toMemberXML_Optimized_C1(m, resRow, mr))
                    m = null

                    if (count % 2000 == 0) {
                        cleanUpGorm()
                    }
                }
            }

            cleanUpGorm()
            outputFile.print("</members>")
            outputFile.close()

            println("File saved! - members.xml")
            output.println("File saved! - members.xml")

            ZipMaker zipMaker = new ZipMaker(SystemPath.generatedFilesPath + "/members.zip")
            zipMaker.addFile(SystemPath.generatedFilesPath + "/members.xml")
            def b = zipMaker.makeZip()
            println "creating zip - members.zip - success=" + b

            processed = 1
            syncFilesReportService.update(SyncEntity.MEMBERS, count)

        } catch (Exception ex) {
            ex.printStackTrace()
            processed = 0
            errors = 1
            println "" + ex.toString()
            output.println(ex.toString())
            logStatusValue = LogStatus.ERROR
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
        }
        output.close()
    }


/**
 * @param m       The Member entity
 * @param resRow  Pre-fetched residency row: [memberId, householdCode, householdName,
 *                                            startType, startDate, endType, endDate]
 *                or null if the member has no current residency.
 * @param mr      Pre-fetched MaritalRelationship entity, or null.
 */
    private String toMemberXML_Optimized_C1(Member m, def resRow, def mr) {

        def householdCode = resRow != null ? resRow[1] : null
        def householdName = resRow != null ? resRow[2] : null
        def startType     = resRow != null ? resRow[3] : null
        def startDate     = resRow != null ? resRow[4] : null
        def endType       = resRow != null ? resRow[5] : null
        def endDate       = resRow != null ? resRow[6] : null

        def spouse = mr != null ? (mr?.memberA_code.equals(m.code) ? mr.memberB : mr.memberA) : null
        m.maritalStatus = maritalRelationshipService.getMaritalStatusFrom(mr)
        m.spouseCode    = spouse?.code
        m.spouseName    = spouse?.name

        return  ("<member>") +
                ((m.code==null || m.code.isEmpty()) ?                   "<code />" : "<code>${m.code}</code>") +
                ((m.name==null || m.name.isEmpty()) ?                   "<name />" : "<name>${m.name}</name>") +
                ((m.gender==null ) ?                                    "<gender />" : "<gender>${m.gender.code}</gender>") +
                ((m.dob==null) ?                                        "<dob />" : "<dob>${StringUtil.format(m.dob)}</dob>") +
                ((m.age==null) ?                                        "<age />" : "<age>${m.age}</age>") +

                ((m.ageAtDeath==null) ?                                 "<ageAtDeath />" : "<ageAtDeath>${m.ageAtDeath}</ageAtDeath>") +

                ((m.motherCode==null || m.motherCode.isEmpty()) ?       "<motherCode />" : "<motherCode>${m.motherCode}</motherCode>") +
                ((m.motherName==null || m.motherName.isEmpty()) ?       "<motherName />" : "<motherName>${m.motherName}</motherName>") +
                ((m.fatherCode==null || m.fatherCode.isEmpty()) ?       "<fatherCode />" : "<fatherCode>${m.fatherCode}</fatherCode>") +
                ((m.fatherName==null || m.fatherName.isEmpty()) ?       "<fatherName />" : "<fatherName>${m.fatherName}</fatherName>") +

                ((m.maritalStatus==null) ?                              "<maritalStatus />" : "<maritalStatus>${m.maritalStatus.code}</maritalStatus>") +
                ((m.spouseCode==null || m.spouseCode.isEmpty()) ?       "<spouseCode />" : "<spouseCode>${m.spouseCode}</spouseCode>") +
                ((m.spouseName==null || m.spouseName.isEmpty()) ?       "<spouseName />" : "<spouseName>${m.spouseName}</spouseName>") +

                ((m.education==null || m.education.isEmpty()) ?         "<education />" : "<education>${m.education}</education>") +
                ((m.religion==null || m.religion.isEmpty()) ?           "<religion />" : "<religion>${m.religion}</religion>") +

                ((m.phonePrimary==null || m.phonePrimary.isEmpty()) ?         "<phonePrimary />" : "<phonePrimary>${m.phonePrimary}</phonePrimary>") +
                ((m.phoneAlternative==null || m.phoneAlternative.isEmpty()) ? "<phoneAlternative />" : "<phoneAlternative>${m.phoneAlternative}</phoneAlternative>") +

                ((householdCode==null || householdCode?.isEmpty()) ? "<householdCode />" : "<householdCode>${householdCode}</householdCode>") +
                ((householdName==null || householdName?.isEmpty()) ? "<householdName />" : "<householdName>${householdName}</householdName>") +

                ((startType==null) ?                                 "<startType />" : "<startType>${startType.code}</startType>") +
                ((startDate==null) ?                                 "<startDate />" : "<startDate>${StringUtil.format(startDate)}</startDate>") +
                ((endType==null)   ?                                 "<endType />"   : "<endType>${endType.code}</endType>") +
                ((endDate==null)   ?                                 "<endDate />"   : "<endDate>${StringUtil.format(endDate)}</endDate>") +

                ((m.entryHousehold==null || m.entryHousehold.isEmpty()) ? "<entryHousehold />" : "<entryHousehold>${m.entryHousehold}</entryHousehold>") +
                ((m.entryType==null) ?                               "<entryType />" : "<entryType>${m.entryType.code}</entryType>") +
                ((m.entryDate==null) ?                               "<entryDate />" : "<entryDate>${StringUtil.format(m.entryDate)}</entryDate>") +

                ((m.headRelationshipType==null) ? "<headRelationshipType />" : "<headRelationshipType>${m.headRelationshipType.code}</headRelationshipType>") +
                ((m.headRelationshipType==null) ? "<isHouseholdHead />" : "<isHouseholdHead>${m.isHouseholdHead()}</isHouseholdHead>") +

                ((m.gpsAccuracy == null)  ? "<gpsAccuracy />"  : "<gpsAccuracy>${m.gpsAccuracy}</gpsAccuracy>") +
                ((m.gpsAltitude == null)  ? "<gpsAltitude />"  : "<gpsAltitude>${m.gpsAltitude}</gpsAltitude>") +
                ((m.gpsLatitude == null)  ? "<gpsLatitude />"  : "<gpsLatitude>${m.gpsLatitude}</gpsLatitude>") +
                ((m.gpsLongitude == null) ? "<gpsLongitude />" : "<gpsLongitude>${m.gpsLongitude}</gpsLongitude>") +

                ((m.collectedId == null) ? "<collectedId />" : "<collectedId>${m.collectedId}</collectedId>") +

                ((m.modules.empty) ? "<modules />" : "<modules>${moduleService.getListModulesAsText(m.modules)}</modules>") +

                ("</member>")
    }
}
