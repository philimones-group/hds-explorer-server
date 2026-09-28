package org.philimone.hds.explorer.server.model.main

import grails.testing.services.ServiceUnitTest
import org.philimone.hds.explorer.services.DataDictionaryService
import org.philimone.hds.explorer.services.DataExportService
import spock.lang.Specification

class DataExportServiceSpec extends Specification implements ServiceUnitTest<DataExportService> {

    void "test PII name masking"() {
        expect:
        service.maskName("Paulo Filimone") == "P*** F***"
        service.maskName("John") == "J***"
        service.maskName("") == ""
        service.maskName(null) == ""
    }

    void "test CSV escaping"() {
        expect:
        service.escapeCsv("Simple Text") == "Simple Text"
        service.escapeCsv("Text, with comma") == "\"Text, with comma\""
        service.escapeCsv("Text with \"quotes\"") == "\"Text with \"\"quotes\"\"\""
        service.escapeCsv(null) == ""
    }

    void "test column name sanitization for statistical packages"() {
        expect:
        service.sanitizeColumnName("Household Member Primary Phone Number") == "household_member_primary_phone_n" // max 32 chars
        service.sanitizeColumnName("gender") == "gender"
        service.sanitizeColumnName("code-123!") == "code_123_"
        service.sanitizeColumnName(null) == "col"
    }

    void "test export SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "member",
                gender: "FEMALE",
                ageMin: 15,
                ageMax: 49,
                activeResidentsOnly: true
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM member m")
        sql.contains("LEFT JOIN residency r")
        sql.contains("LEFT JOIN head_relationship hr")
        sql.contains("r.end_type = 'NA' OR r.end_date IS NULL")
        sql.contains("m.gender = 'FEMALE'")
        sql.contains("TIMESTAMPDIFF(YEAR, m.dob, CURRENT_DATE) >= 15")
        sql.contains("TIMESTAMPDIFF(YEAR, m.dob, CURRENT_DATE) <= 49")
    }

    void "test Stata do-script syntax generation"() {
        setup:
        DataDictionaryService mockDictService = Mock(DataDictionaryService)
        service.dataDictionaryService = mockDictService

        DataDictionaryService.TableMetadata meta = new DataDictionaryService.TableMetadata(tableName: "member")
        meta.addColumn(new DataDictionaryService.ColumnMetadata(
                tableName: "member",
                columnName: "gender",
                label: "Gender of Individual",
                enumTypeName: "Gender"
        ))

        DataDictionaryService.EnumTypeMetadata enumType = new DataDictionaryService.EnumTypeMetadata(enumTypeName: "Gender")
        enumType.addOption(new DataDictionaryService.EnumOptionMetadata(code: "MALE", label: "Male"))
        enumType.addOption(new DataDictionaryService.EnumOptionMetadata(code: "FEMALE", label: "Female"))

        mockDictService.getTableMetadata("member") >> meta
        mockDictService.getEnumTypeMetadata("Gender") >> enumType

        DataExportService.ExportRequest req = new DataExportService.ExportRequest(datasetName: "member", format: "STATA")

        when:
        String script = service.generateSyntaxScript(req)

        then:
        script.contains("import delimited using \"member_data.csv\"")
        script.contains("label variable gender \"Gender of Individual\"")
        script.contains("label define gender_lbl MALE \"Male\" FEMALE \"Female\"")
        script.contains("label values gender gender_lbl")
    }
}
