package org.philimone.hds.explorer.server.model.main.extension

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class CoreExtensionServiceSpec extends Specification implements ServiceUnitTest<CoreExtensionService>, DataTest, AutowiredTest{

    def setup() {
        mockDomains RawHousehold, CoreFormExtensionModel, CoreFormExtension
        
        setupExtensions()
        setupModels()
    }

    def setupExtensions() {
        String xformPath = "/home/paul/local/personal/codes/hds-explorer/consulting-deployments/household_ext.xml" as String;
        
        def coreFormExt = new CoreFormExtension()
        coreFormExt.formName = "Household Registration Extension Form"
        coreFormExt.formId = "rawHousehold"
        coreFormExt.extFormId = "household_ext"
        coreFormExt.extFormDefinition = Files.readAllBytes(Paths.get(xformPath))
        coreFormExt.required=false
        coreFormExt.enabled=true
        coreFormExt.columnsMapping = "core_form_id<#>rawHousehold;collected_id<#>#id;household_code<#>#householdCode;household_name<#>#householdName;visit_code<#>#visitCode;head_code<#>#headCode;head_name<#>#headName"
        coreFormExt.coreForm = CoreForm.HOUSEHOLD_FORM

        def result = coreFormExt.save(flush:true)

        println "Saving CoreFormExtension ${result}"
    }

    def setupHousehold() {

    }

    def setupModels() {
        def extension = CoreFormExtension.list().first()

        println "extension: ${extension}"

        service.generateDatabaseModel(extension)
    }
    
    def cleanup() {
    }

    void "test InsertHouseholdOutput"() {

        println("models: ${CoreFormExtensionModel.count()}")

        String xformInstance = "/home/paul/local/personal/codes/hds-explorer/consulting-deployments/household_ext_instance.xml";
        def rawHousehold = new RawHousehold(householdCode: "TESTING")
        rawHousehold.extensionForm = Files.readAllBytes(Paths.get(xformInstance))

        def map = service.insertHouseholdExtension(rawHousehold, null)

        map.each {
            println "${it.key}: ${it.value}"
        }


        expect:"greater than zero"
            map.size() > 0
    }
}
