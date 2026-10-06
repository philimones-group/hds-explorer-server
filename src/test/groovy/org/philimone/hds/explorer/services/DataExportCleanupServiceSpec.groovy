package org.philimone.hds.explorer.services

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class DataExportCleanupServiceSpec extends Specification implements ServiceUnitTest<DataExportCleanupService>{

    def setup() {
    }

    def cleanup() {
    }

    void "test cleanup service instantiation"() {
        expect:
        service != null
    }
}
