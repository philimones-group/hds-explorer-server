package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.io.SystemPath

class DataExportCleanupJob {

    static triggers = {
        // Run daily at 2:00 AM
        cron name: 'exportCleanupTrigger', cronExpression: '0 0 2 * * ?'
    }

    def execute() {
        log.info("Starting DataExportCleanupJob: Cleaning up export files older than 48 hours...")

        try {
            File exportDir = new File(SystemPath.externalDocsPath)

            if (exportDir.exists() && exportDir.isDirectory()) {
                long cutoffTime = System.currentTimeMillis() - (48 * 60 * 60 * 1000L) // 48 hours ago
                int deletedCount = 0

                exportDir.listFiles().each { File f ->
                    if (f.isFile() && f.lastModified() < cutoffTime) {
                        String name = f.name.toLowerCase()
                        // Only clean up generated export bundle files
                        if (name.endsWith(".zip") || name.contains("export") || name.endsWith(".xlsx") || name.endsWith(".csv")) {
                            if (f.delete()) {
                                deletedCount++
                                log.info("Deleted expired export file: ${f.name}")
                            } else {
                                log.warn("Failed to delete expired export file: ${f.name}")
                            }
                        }
                    }
                }

                log.info("DataExportCleanupJob completed. Deleted ${deletedCount} expired export files from ${exportDir.absolutePath}.")
            } else {
                log.info("DataExportCleanupJob: External docs directory ${exportDir.absolutePath} does not exist yet.")
            }
        } catch (Exception e) {
            log.error("DataExportCleanupJob error: ${e.message}", e)
        }
    }
}
