package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.io.SystemPath

/**
 * This controller exposes the generated XML/Zip files to be downloaded and controls the export views tasks
 */
class ExportFilesController {

    static responseFormats = ['xml']

    def exportFilesService

    def households = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "households.xml")
        render file: file
    }

    def individuals = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "individuals.xml")
        render file: file
    }

    def settings = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "settings.xml")
        render file: file
    }

    def users = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "users.xml")
        render file: file
    }

    def trackinglists = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.xml")
        render file: file
    }

    def stats = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "stats.xml")
        render file: file
    }

    /* ZIP Files*/

    def householdsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "households.zip")
        render file: file, fileName: "households.zip"
    }

    def individualsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "individuals.zip")
        render file: file, fileName: "individuals.zip"
    }

    def settingsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "settings.zip")
        render file: file, fileName: "settings.zip"
    }

    def usersZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "users.zip")
        render file: file, fileName: "users.zip"
    }

    def trackinglistsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.zip")
        render file: file, fileName: "trackinglists.zip"
    }

    def statsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "stats.zip")
        render file: file, fileName: "stats.zip"
    }



}
