package org.philimone.hds.explorer.services

import grails.rest.Link
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.json.dashboard.CoreFormStatus
import org.philimone.hds.explorer.server.model.json.dashboard.FieldworkerStatus
import org.philimone.hds.explorer.server.model.json.dashboard.PieStatus
import org.philimone.hds.explorer.server.model.json.dashboard.PyramidBar
import org.philimone.hds.explorer.server.model.json.dashboard.Totals
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.Death
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.OutMigration
import org.philimone.hds.explorer.server.model.main.Residency

import javax.transaction.Transactional
import java.time.LocalDate

@Transactional
class DashboardService {

    def generalUtilitiesService

    static pyramidGroups = [
             new PyramidBar(id: 1, minAge: 0, maxAge: 4, ageRange: "0-4"),
             new PyramidBar(id: 2, minAge: 5, maxAge: 9, ageRange: "5-9"),
             new PyramidBar(id: 3, minAge: 10, maxAge: 14, ageRange: "10-14"),
             new PyramidBar(id: 4, minAge: 15, maxAge: 19, ageRange: "15-19"),
             new PyramidBar(id: 5, minAge: 20, maxAge: 24, ageRange: "20-24"),
             new PyramidBar(id: 6, minAge: 25, maxAge: 29, ageRange: "25-29"),
             new PyramidBar(id: 7, minAge: 30, maxAge: 34, ageRange: "30-34"),
             new PyramidBar(id: 8, minAge: 35, maxAge: 39, ageRange: "35-39"),
             new PyramidBar(id: 9, minAge: 40, maxAge: 44, ageRange: "40-44"),
             new PyramidBar(id: 10, minAge: 45, maxAge: 49, ageRange: "45-49"),
             new PyramidBar(id: 11, minAge: 50, maxAge: 54, ageRange: "50-54"),
             new PyramidBar(id: 12, minAge: 55, maxAge: 59, ageRange: "55-59"),
             new PyramidBar(id: 13, minAge: 60, maxAge: 64, ageRange: "60-64"),
             new PyramidBar(id: 14, minAge: 65, maxAge: 69, ageRange: "65-69"),
             new PyramidBar(id: 15, minAge: 70, maxAge: 74, ageRange: "70-74"),
             new PyramidBar(id: 16, minAge: 75, maxAge: 79, ageRange: "75-79"),
             new PyramidBar(id: 17, minAge: 80, maxAge: 84, ageRange: "80-84"),
             new PyramidBar(id: 18, minAge: 85, maxAge: 89, ageRange: "85-89"),
             new PyramidBar(id: 19, minAge: 90, maxAge: 94, ageRange: "90-94"),
             new PyramidBar(id: 20, minAge: 95, maxAge: 99, ageRange: "95-99"),
             new PyramidBar(id: 21, minAge: 100, maxAge: Integer.MAX_VALUE, ageRange: "100+")
    ]

    Totals retrieveTotals() {
        def totals = new Totals()

        totals.households = Household.count()
        totals.individuals = Member.count()-1 //remove UNK

        totals.residents = Residency.countByEndType(ResidencyEndType.NOT_APPLICABLE)
        totals.residents_male = Residency.executeQuery("select count(r.id) from Residency r where r.endType=?0 and r.member.gender=?1", ResidencyEndType.NOT_APPLICABLE, Gender.MALE).first()
        totals.residents_female = Residency.executeQuery("select count(r.id) from Residency r where r.endType=?0 and r.member.gender=?1", ResidencyEndType.NOT_APPLICABLE, Gender.FEMALE).first()

        totals.outmigrations = OutMigration.countByMigrationType(OutMigrationType.EXTERNAL)
        totals.outmigrations_male = OutMigration.executeQuery("select count(o.id) from OutMigration o where o.migrationType=?0 and o.member.gender=?1", OutMigrationType.EXTERNAL, Gender.MALE).first()
        totals.outmigrations_female = OutMigration.executeQuery("select count(o.id) from OutMigration o where o.migrationType=?0 and o.member.gender=?1", OutMigrationType.EXTERNAL, Gender.FEMALE).first()

        totals.deaths = Death.count()
        totals.deaths_male = Death.executeQuery("select count(d.id) from Death d where d.member.gender=?0", Gender.MALE).first()
        totals.deaths_female = Death.executeQuery("select count(d.id) from Death d where d.member.gender=?0", Gender.FEMALE).first()
        
        
        return totals
    }

    List<PyramidBar> retrievePopulationPyramid() {

        def groups = pyramidGroups.clone() as List<PyramidBar>

        groups.each { bar ->
            bar.male = Residency.executeQuery("select count(r.id) from Residency r where r.endType=?1 and r.member.gender=?2 and (floor(datediff(?0, r.member.dob)/365.25))>?3 and (floor(datediff(?0, r.member.dob)/365.25))<?4 ",
                                              LocalDate.now(), ResidencyEndType.NOT_APPLICABLE, Gender.MALE, bar.minAge, bar.maxAge).first()

            //println "bar(${bar.ageRange}) = male(${bar.male})"

            bar.female = Residency.executeQuery("select count(r.id) from Residency r where r.endType=?1 and r.member.gender=?2 and (floor(datediff(?0, r.member.dob)/365.25))>?3 and (floor(datediff(?0, r.member.dob)/365.25))<?4 ",
                    LocalDate.now(), ResidencyEndType.NOT_APPLICABLE, Gender.FEMALE, bar.minAge, bar.maxAge).first()

            bar.female *= -1
        }

        return groups
    }

    List<FieldworkerStatus> retrieveFieldworkerStatus() {
        def list = new ArrayList<FieldworkerStatus>()

        User.list().eachWithIndex { user, index ->
            def status = new FieldworkerStatus(id: index, code: user.username, name: user.fullname)

            //start counting collected
            status.formCollected =  RawChangeHead.countByCollectedBy(user.username)
            status.formCollected += RawDeath.countByCollectedBy(user.username)
            status.formCollected += RawExternalInMigration.countByCollectedBy(user.username)
            status.formCollected += RawHousehold.countByCollectedBy(user.username)
            status.formCollected += RawIncompleteVisit.countByCollectedBy(user.username)
            status.formCollected += RawInMigration.countByCollectedBy(user.username)
            status.formCollected += RawMaritalRelationship.countByCollectedBy(user.username)
            status.formCollected += RawMemberEnu.countByCollectedBy(user.username)
            status.formCollected += RawOutMigration.countByCollectedBy(user.username)
            status.formCollected += RawPregnancyOutcome.countByCollectedBy(user.username)
            status.formCollected += RawPregnancyRegistration.countByCollectedBy(user.username)
            status.formCollected += RawRegion.countByCollectedBy(user.username)
            status.formCollected += RawVisit.countByCollectedBy(user.username)

            //start counting synchronized
            status.formSynchronized =  RawChangeHead.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawDeath.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawExternalInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawHousehold.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawIncompleteVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawMaritalRelationship.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawMemberEnu.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawOutMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawPregnancyOutcome.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawPregnancyRegistration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawRegion.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)
            status.formSynchronized += RawVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.SUCCESS)

            //start counting pending
            status.formPending =  RawChangeHead.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawDeath.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawExternalInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawHousehold.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawIncompleteVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawMaritalRelationship.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawMemberEnu.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawOutMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawPregnancyOutcome.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawPregnancyRegistration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawRegion.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)
            status.formPending += RawVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.NOT_PROCESSED)

            //start counting errors
            status.formError =  RawChangeHead.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawDeath.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawExternalInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawHousehold.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawIncompleteVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawMaritalRelationship.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawMemberEnu.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawOutMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawPregnancyOutcome.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawPregnancyRegistration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawRegion.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)
            status.formError += RawVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.ERROR)

            //start counting invalidated
            status.formInvalidated =  RawChangeHead.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawDeath.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawExternalInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawHousehold.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawIncompleteVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawInMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawMaritalRelationship.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawMemberEnu.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawOutMigration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawPregnancyOutcome.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawPregnancyRegistration.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawRegion.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)
            status.formInvalidated += RawVisit.countByCollectedByAndProcessedStatus(user.username, ProcessedStatus.INVALIDATED)

            list.add(status)
        }

        return list
    }

    Map<String, CoreFormStatus> retrieveCoreFormStatus() {
        def map = new LinkedHashMap<String, CoreFormStatus>()

        def rawDomains = ["RawRegion", "RawHousehold", "RawVisit", "RawMemberEnu", "RawMaritalRelationship", "RawInMigration", "RawExternalInMigration", "RawOutMigration", "RawPregnancyRegistration", "RawPregnancyOutcome", "RawChangeHead", "RawDeath", "RawIncompleteVisit"]


        rawDomains.eachWithIndex { domain, index ->
            def status = new CoreFormStatus(id: index, code: domain, name: domain)

            //start counting collected
            status.formCollected =  RawHousehold.executeQuery("select count(r.id) from "+domain+" r").first()
            status.formSynchronized = RawHousehold.executeQuery("select count(r.id) from "+domain+" r where r.processedStatus=?0", ProcessedStatus.SUCCESS).first()
            status.formPending = RawHousehold.executeQuery("select count(r.id) from "+domain+" r where r.processedStatus=?0", ProcessedStatus.NOT_PROCESSED).first()
            status.formError = RawHousehold.executeQuery("select count(r.id) from "+domain+" r where r.processedStatus=?0", ProcessedStatus.ERROR).first()
            status.formInvalidated = RawHousehold.executeQuery("select count(r.id) from "+domain+" r where r.processedStatus=?0", ProcessedStatus.INVALIDATED).first()

            map.put(domain, status)
        }

        return map
    }

    //Last 15 days
    //Last 30 days
    //Last 3 months
    //This Round

    List<PieStatus> retrieveEducationRates() {
        def list = new ArrayList<PieStatus>()

        def educationOpts = CoreFormColumnOptions.findAllByColumnName("education", [sort:"ordinal", order: "asc"])

        def totalMembers = Member.count() - 1

        educationOpts.each { opt ->
            def count = Member.countByEducation(opt.optionValue)
            def label = generalUtilitiesService.getMessageWeb(opt.optionLabelCode)
            label = label==null ? opt.optionLabel : label

            def status = new PieStatus(id: opt.ordinal, name: label, total: Math.round(count*100D / totalMembers*1D))
            list.add(status)
        }

        return list
    }

    List<PieStatus> retrieveReligionRates() {
        def list = new ArrayList<PieStatus>()

        def religionOpts = CoreFormColumnOptions.findAllByColumnName("religion", [sort:"ordinal", order: "asc"])

        def totalMembers = Member.count() - 1

        religionOpts.each { opt ->
            def count = Member.countByReligion(opt.optionValue)
            def label = generalUtilitiesService.getMessageWeb(opt.optionLabelCode)
            label = label==null ? opt.optionLabel : label

            def status = new PieStatus(id: opt.ordinal, name: label, total: Math.round(count*100D / totalMembers*1D))
            list.add(status)
        }

        return list
    }
}