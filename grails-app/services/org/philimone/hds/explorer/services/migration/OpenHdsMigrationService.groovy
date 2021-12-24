package org.philimone.hds.explorer.services.migration

import grails.gorm.transactions.Transactional

/*
 * This service will belong to a small program called hds-migrator (that will contain OpenHDS Tables and HDS-Explorer Raw Tables)
 * The script/program will export OpenHDS data into HDS-XPLORER Raw Tables from there a RawBatchExecution will be executed
 */

@Transactional
class OpenHdsMigrationService {

    def exportDatabase() {

        //export Regions
        //export Households
        //export Member Enu
        //export



    }
}
