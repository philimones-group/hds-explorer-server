package org.philimone.hds.explorer.services

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.DataExportReport
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class DataExportQueryBuilderSpec extends Specification implements ServiceUnitTest<DataExportService>, DataTest {

    def setup() {
        mockDomains(Region, CoreFormExtension, CoreFormExtensionModel, DataExportReport)

        // Mock Region for level-aware spatial filtering
        Region region = new Region(code: "PEMBA", name: "Pemba District", hierarchyLevel: RegionLevel.HIERARCHY_2)
        region.save(flush: true)
    }

    @Unroll
    void "test buildExportSql for pre-joined view dataset: #datasetName"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: datasetName,
                gender: "FEMALE",
                ageMin: 15,
                ageMax: 49,
                regionCode: "PEMBA",
                referenceDate: LocalDate.now(),
                randomSamplePercent: 50.0,
                activeResidentsOnly: true
        )

        when:
        String sql = service.buildExportSql(request)
        def session = service.sessionFactory?.currentSession
        List rows = session ? session.createNativeQuery(sql).setMaxResults(1).list() : []

        then:
        noExceptionThrown()
        sql != null
        !sql.trim().isEmpty()
        sql.contains("SELECT")
        sql.contains("WHERE")

        where:
        datasetName << [
                "INDEPTH_CORE_RESIDENCY",
                "RESIDENCY_PERSON_TIME",
                "INDEPTH_MATERNAL_PREGNANCY",
                "MATERNAL_PREGNANCY",
                "INDEPTH_HOUSEHOLD_COMPOSITION",
                "HOUSEHOLD_COMPOSITION"
        ]
    }

    @Unroll
    void "test buildExportSql for core database final table: #datasetName"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: datasetName,
                gender: "FEMALE",
                ageMin: 15,
                ageMax: 49,
                regionCode: "PEMBA",
                referenceDate: LocalDate.now(),
                randomSamplePercent: 50.0,
                activeResidentsOnly: true
        )

        when:
        String sql = service.buildExportSql(request)
        def session = service.sessionFactory?.currentSession
        List rows = session ? session.createNativeQuery(sql).setMaxResults(1).list() : []

        then:
        noExceptionThrown()
        sql != null
        !sql.trim().isEmpty()
        sql.contains("SELECT")
        sql.contains("WHERE")

        where:
        datasetName << [
                "death",
                "enumeration",
                "household",
                "head_relationship",
                "in_migration",
                "incomplete_visit",
                "household_relocation",
                "household_proxy_head",
                "marital_relationship",
                "member",
                "out_migration",
                "pregnancy_child",
                "pregnancy_outcome",
                "pregnancy_registration",
                "pregnancy_visit",
                "pregnancy_visit_child",
                "region",
                "region_head_relationship",
                "residency",
                "round",
                "visit",
                "_user"
        ]
    }

    @Unroll
    void "test buildExportSql for dynamic XLS extension form: #datasetName"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: datasetName,
                gender: "FEMALE",
                ageMin: 15,
                ageMax: 49,
                regionCode: "PEMBA",
                referenceDate: LocalDate.now(),
                randomSamplePercent: 50.0,
                activeResidentsOnly: true
        )

        when:
        String sql = service.buildExportSql(request)
        def session = service.sessionFactory?.currentSession
        List rows = session ? session.createNativeQuery(sql).setMaxResults(1).list() : []

        then:
        noExceptionThrown()
        sql != null
        !sql.trim().isEmpty()
        sql.contains("SELECT")
        sql.contains("WHERE")

        where:
        datasetName << [
                "change_head_ext",
                "change_region_head_ext",
                "death_ext",
                "household_ext",
                "household_proxy_head_ext",
                "household_relocation_ext",
                "in_migration_ext",
                "incomplete_visit_ext",
                "marital_relationship_ext",
                "member_ext",
                "out_migration_ext",
                "pregnancy_outcome_ext",
                "pregnancy_registration_ext",
                "pregnancy_visit_ext",
                "region_ext",
                "sample_empty",
                "visit_ext"
        ]
    }
}
