package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.io.readers.CSVReader
import net.betainteractive.io.writers.ZipMaker
import net.betainteractive.utilities.StringUtil

@Transactional
class DataSetService {

    List<String> getColumns(String filenameCsv) {
        CSVReader reader = new CSVReader(filenameCsv, true, ",")
        def cols = reader.getFieldNames()
        reader.close()

        return cols
    }

    String getDatasetName(String filename){
        int i = filename.lastIndexOf(".")
        filename = i==-1 ? filename : filename.substring(0, i)
        filename = StringUtil.removeAcentuation(filename)
        filename = StringUtil.removeCamelCaseAndCapitalize(filename)

        return filename
    }

    boolean containsDatasetWith(String name){
        Dataset.countByName(name) > 0
    }

    List<String> getDatasetColumnsWith(String name){
        def dataset = Dataset.findByName(name)
        if (dataset != null){

            return getColumns(dataset.filename)
        }

        return null
    }

    List<String> getDatasetNames(){
        return Dataset.findAllByEnabled(true).collect{ it.name }
    }

    def createZipFile(Dataset dataSet){

        //zip file
        ZipMaker zipMaker = new ZipMaker(dataSet.compressedFilename)
        zipMaker.addFile(dataSet.filename)
        def b = zipMaker.makeZip()

        println "creating dataset zip file - ${dataSet.compressedFilename} - success="+b
    }
}
