package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.io.readers.CSVReader
import net.betainteractive.io.writers.ZipMaker
import net.betainteractive.utilities.StringUtil

@Transactional
class DatasetService {

    LinkedHashMap<String,String> getColumns(String filenameCsv) {
        CSVReader reader = new CSVReader(filenameCsv, true, ",")
        def cols = reader.getFieldNames()
        reader.close()


        def map = new LinkedHashMap<String,String>()

        cols.each { name ->
            def spt = name.split(":")
            //println(name)
            if (spt.length > 1) {
                map.put(spt[0], spt[1])
            } else {
                map.put(spt[0], spt[0])
            }
        }

        return map
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

    def copyFileAndRemoveLabels(String tmpFile, String newFile) {

        String regex_delimiter = ","+'(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)';

        Scanner input = new Scanner(new File(tmpFile))
        PrintStream output = new PrintStream(newFile)

        int i = 0;
        while (input.hasNextLine()) {
            def line = input.nextLine()
            //println(line)
            if (i==0){
                def cols = line.split(regex_delimiter)
                line = ""
                cols.each {
                    line += (line.empty ? "":",") + it.replaceAll(":.+", "")
                }
            }

            output.println(line)
            i++;
        }

        input.close()
        output.close()

    }
}
