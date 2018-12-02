package org.philimone.hds.explorer.server.model.main

import com.google.common.io.Files
import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.Application
import org.philimone.hds.explorer.io.SystemPath

@Transactional
class TrackingListService {

    /**
     * For tests purposes only
     */
    def copySampleFilesToExternalDocsPath() {


        def fileUrl1 = Application.class.classLoader.getResource("samples/tracking_list_sample_1.xlsx")
        def fileUrl2 = Application.class.classLoader.getResource("samples/tracking_list_sample_2.xlsx")
        def fileTo1 = new File(SystemPath.externalDocsPath + File.separator + "tracking_list_sample_1.xlsx")
        def fileTo2 = new File(SystemPath.externalDocsPath + File.separator + "tracking_list_sample_2.xlsx")

        //println "1 - ${fileUrl1}"
        //println "2 - ${fileUrl2}"

        def file1 = new File(fileUrl1.toURI())
        def file2 = new File(fileUrl2.toURI())

        Files.copy(file1, fileTo1 )
        Files.copy(file2, fileTo2 )
    }
}
