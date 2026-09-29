package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.philimone.hds.explorer.services.DataDictionaryService
import org.philimone.hds.forms.model.enums.SensitiveType
import spock.lang.Specification

class DataDictionaryServiceSpec extends Specification implements ServiceUnitTest<DataDictionaryService>, DataTest {

    def setup() {
        mockDomains(Region, CoreFormExtension, CoreFormExtensionModel)
    }

    void "test enum type name extraction"() {
        expect:
        service.extractEnumTypeName("DeathCause (DeathCause)") == "DeathCause"
        service.extractEnumTypeName("Gender (Gender)") == "Gender"
        service.extractEnumTypeName("VARCHAR(255)") == "255"
        service.extractEnumTypeName("enum(identity) (Gender)") == "Gender"
        service.extractEnumTypeName(null) == null
    }

    void "test SensitiveType enum resolution"() {
        expect:
        SensitiveType.getFrom("person_name") == SensitiveType.PERSON_NAME
        SensitiveType.getFrom("phone_number") == SensitiveType.PHONE_NUMBER
        SensitiveType.getFrom("gps") == SensitiveType.GPS
        SensitiveType.getFrom("date") == SensitiveType.DATE
        SensitiveType.getFrom("not_applicable") == SensitiveType.NOT_APPLICABLE
        SensitiveType.getFrom(null) == SensitiveType.NOT_APPLICABLE
        SensitiveType.getFrom("invalid") == SensitiveType.NOT_APPLICABLE
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
                sensitiveType: SensitiveType.NOT_APPLICABLE
        ))
        tableMeta.addColumn(new DataDictionaryService.ColumnMetadata(
                tableName: "member",
                columnName: "name",
                dataType: "String",
                description: "Full name",
                label: "Full Name",
                sensitiveType: SensitiveType.PERSON_NAME
        ))

        when:
        service.@tablesMap.put("member", tableMeta)

        then:
        service.getTableMetadata("member") != null
        service.getTableMetadata("member").description == "Individual table"
        service.getColumnMetadata("member", "code").label == "Member Code"
        service.getColumnMetadata("member", "name").sensitiveType == SensitiveType.PERSON_NAME
    }
}
