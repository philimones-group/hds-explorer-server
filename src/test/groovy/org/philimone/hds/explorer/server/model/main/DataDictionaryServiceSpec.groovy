package org.philimone.hds.explorer.server.model.main

import grails.testing.services.ServiceUnitTest
import org.philimone.hds.explorer.services.DataDictionaryService
import spock.lang.Specification

class DataDictionaryServiceSpec extends Specification implements ServiceUnitTest<DataDictionaryService> {

    void "test enum type name extraction"() {
        expect:
        service.extractEnumTypeName("enum(string) (DeathCause)") == "DeathCause"
        service.extractEnumTypeName("enum(identity) (Gender)") == "Gender"
        service.extractEnumTypeName("VARCHAR(255)") == null
        service.extractEnumTypeName(null) == null
    }

    void "test sensitivity tier classification"() {
        expect:
        service.classifySensitivityTier("first_name") == 3 // Tier 3 Direct PII
        service.classifySensitivityTier("phone_primary") == 3 // Tier 3 Direct PII
        service.classifySensitivityTier("gps_latitude") == 3 // Tier 3 Direct PII
        service.classifySensitivityTier("dob") == 2 // Tier 2 Indirect Identifier
        service.classifySensitivityTier("start_date") == 2 // Tier 2 Indirect Identifier
        service.classifySensitivityTier("household_code") == 2 // Tier 2 Indirect Identifier
        service.classifySensitivityTier("gender") == 1 // Tier 1 Standard
        service.classifySensitivityTier("education") == 1 // Tier 1 Standard
    }

    void "test table metadata registration and column lookup"() {
        setup:
        DataDictionaryService.TableMetadata tableMeta = new DataDictionaryService.TableMetadata(
                tableName: "member",
                description: "Individual table"
        )
        tableMeta.addColumn(new DataDictionaryService.ColumnMetadata(
                tableName: "member",
                columnName: "code",
                dataType: "String",
                description: "Member code",
                label: "Member Code",
                sensitivityTier: 2
        ))
        tableMeta.addColumn(new DataDictionaryService.ColumnMetadata(
                tableName: "member",
                columnName: "name",
                dataType: "String",
                description: "Full name",
                label: "Full Name",
                sensitivityTier: 3
        ))

        when:
        service.getTablesMap().put("member", tableMeta)

        then:
        service.getTableMetadata("member") != null
        service.getTableMetadata("member").description == "Individual table"
        service.getColumnMetadata("member", "code").label == "Member Code"
        service.getColumnMetadata("member", "name").sensitivityTier == 3
    }
}
