package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.hibernate.SessionFactory
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.services.DataExportService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@Integration
@Rollback
class DataExportIntegrationSpec extends Specification {

    DataExportService dataExportService
    SessionFactory sessionFactory

    def setup() {
        // Create test Region for level-aware spatial filtering
        Region region = new Region(
                code: "PEMBA",
                name: "Pemba District",
                hierarchyLevel: RegionLevel.HIERARCHY_2
        )
        region.save(flush: true)
    }

    @Unroll
    void "test real database execution for pre-joined view: #datasetName"() {
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
        String sql = dataExportService.buildExportSql(request)
        List result = sessionFactory.currentSession.createNativeQuery(sql).setMaxResults(1).list()

        then:
        noExceptionThrown()
        result != null

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
    void "test real database execution for core final table: #datasetName"() {
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
        String sql = dataExportService.buildExportSql(request)
        List result = sessionFactory.currentSession.createNativeQuery(sql).setMaxResults(1).list()

        then:
        noExceptionThrown()
        result != null

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
    void "test real database execution for dynamic XLS extension form: #datasetName"() {
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
        String sql = dataExportService.buildExportSql(request)
        List result = sessionFactory.currentSession.createNativeQuery(sql).setMaxResults(1).list()

        then:
        noExceptionThrown()
        result != null

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
                "visit_ext"
        ]
    }
}
