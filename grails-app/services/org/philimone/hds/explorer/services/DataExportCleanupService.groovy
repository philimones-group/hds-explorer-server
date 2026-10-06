package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.philimone.hds.explorer.io.SystemPath
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@CompileStatic
@Transactional
class DataExportCleanupService {

    static lazyInit = false

    /**
     * Automatic cleanup of exported files
     * Fires everyday at 00:24 AM
     */
    @Scheduled(cron = "0 24 0 * * ?")
    def cleanExportFiles() {
        println("Starting DataExportCleanupJob: Cleaning up export files older than 7 days...")
        try {
            File exportDir = new File(SystemPath.externalDocsPath)

            if (exportDir.exists() && exportDir.isDirectory()) {
                long cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L) // 7 days ago (168 hours)
                int deletedCount = 0

                exportDir.listFiles().each { File f ->
                    if (f.isFile() && f.lastModified() < cutoffTime) {
                        String name = f.name.toLowerCase()
                        // Only clean up generated export bundle files
                        if (name.endsWith(".zip") || name.contains("export") || name.endsWith(".xlsx") || name.endsWith(".csv")) {
                            if (f.delete()) {
                                deletedCount++
                                println("Deleted expired export file: ${f.name}")
                            } else {
                                println("Failed to delete expired export file: ${f.name}")
                            }
                        }
                    }
                }

                println("DataExportCleanupJob completed. Deleted ${deletedCount} expired export files from ${exportDir.absolutePath}.")
            } else {
                println("DataExportCleanupJob: External docs directory ${exportDir.absolutePath} does not exist yet.")
            }
        } catch (Exception e) {
            println("DataExportCleanupJob error: ${e.message}")
        }
    }
}
