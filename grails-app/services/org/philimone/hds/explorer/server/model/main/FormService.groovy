package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.enums.FormType
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.json.JRegionLevel
import org.philimone.hds.explorer.server.model.settings.ApplicationParam

import java.util.regex.Pattern

@Transactional
class FormService {

    Form get(Serializable id){
        Form.get(id)
    }

    List<Form> list(Map args){
        Form.list(args)
    }

    Long count(){
        Form.count()
    }

    void delete(Serializable id){
        get(id).delete()
    }

    void deleteMappings(Serializable id){
        FormMapping.executeUpdate("delete from FormMapping f where f.form.id=?0", [id])
    }

    void deleteGroupMappings(Serializable id){
        FormGroupMapping.executeUpdate("delete from FormGroupMapping f where f.groupForm.id=?0", [id])
    }

    Form save(Form form){
        form.save(flush:true)
    }

    void updateModules(Form form, List<Module> modules) {
        def modList = form.modules.collect { it}
        //delete previous modules
        modList.each {
            form.removeFromModules(it)
        }
        //add new modules
        modules.each {
            form.addToModules(it.code)
        }
    }

    List<JRegionLevel> getRegionLevels(){
        def list = []
        ApplicationParam.executeQuery("select p from ApplicationParam p where p.name like '%hierarchy%' and p.value is not null order by p.name asc" ).each {
            list << new JRegionLevel(level: it.name, name: it.value)
        }
        return list
    }

    String getRegionLevelName(String level){
        ApplicationParam.findByName(level)?.value
    }

    List getRegisteredRepeatGroupVar(Form form) {
        def list = []

        def groups = getRepeatGroupSpecialConstants().collect{ it.value }

        FormMapping.findByFormAndColumnNameInList(form, groups).each {
            list.add(it.formVariableName)
        }

        return list
    }

    List<JConstant> getRepeatGroupSpecialConstants() {
        def list = []

        list << new JConstant(name: "Household Resident Members <repeat group>", value: "HouseholdResidentMembers")
        list << new JConstant(name: "Household Dead Members <repeat group>", value: "HouseholdDeadMembers")
        list << new JConstant(name: "Household OutMigrated Members <repeat group>", value: "HouseholdExtMembers")
        list << new JConstant(name: "All Household Members <repeat group>", value: "HouseholdAllMembers")

        return list
    }

    List<JConstant> getSpecialConstants(){ //All Special Constants
        def list = []

        list << new JConstant(name: "Timestamp     : Date[Y-M-D H:M:S]", value: "Timestamp")
        list << new JConstant(name: "Household Resident Members <repeat group>", value: "HouseholdResidentMembers")
        list << new JConstant(name: "Household Dead Members <repeat group>", value: "HouseholdDeadMembers")
        list << new JConstant(name: "Household OutMigrated Members <repeat group>", value: "HouseholdExtMembers")
        list << new JConstant(name: "All Household Members <repeat group>", value: "HouseholdAllMembers")
        list << new JConstant(name: "Member Exists : Boolean[true, false]]", value: "MemberExists")

        return list
    }

    List<String> getMappingTableList() { ["Household","Member","Region","User","FollowUp-List","Form-Group"] }

    String generateGroupId(String formName) {
        //FG0001
        def codebase = "FG"
        def codes = Form.findAllByFormIdLike("${codebase}%", [sort:'formId', order: 'asc']).collect{ t -> t.formId}
        def newcode = "${codebase}"
        def increment = 0

        if (codes.size() == 0) {
            //increment = 1

        } else {
            String lastCode = codes.last()
            lastCode = lastCode.replace("FG", "")

            println("lastCode removed ${codebase}: ${lastCode}")

            try {
                increment = Integer.parseInt(lastCode)
            } catch (Exception e) {
                e.printStackTrace()

            }
        }

        newcode = "${codebase}${String.format("%04d", ++increment)}"

        while (codes.contains(newcode)) {
            newcode = "${codebase}${String.format("%04d", ++increment)}"
        }

        return newcode

    }

    def formsList(params){
        //params.term = '01-0101'
        String term = params.term
        String regex = '\\d\\d-\\d\\d\\d\\d-\\d\\d\\d\\-\\d\\d'
        def pattern = Pattern.compile(regex);
        def matcher = pattern.matcher(term);

        def queryFormId = {
            eq("formType", FormType.REGULAR)
            and {
                like("formId", "${term}%") // term is the parameter send by jQuery autocomplete
            }
            projections { // good to select only the required columns.
                property("formId")
                property("formName")
                property("formDescription")
            }
        }

        def queryName = {
            eq("formType", FormType.REGULAR)
            and {
                like("formName", "%${term}%") // term is the parameter send by jQuery autocomplete
            }
            projections { // good to select only the required columns.
                property("formId")
                property("formName")
                property("formDescription")
            }
        }

        def query = (term != null && (matcher.matches() || matcher.hitEnd())) ? queryFormId  : queryName



        def recordsList = Form.createCriteria().list(query) // execute  to the get the list of companies
        def list = [] // to add each company details

        recordsList.each {
            def map = [:] // add to map. jQuery autocomplete expects the JSON object to be with id/label/value.
            map.put("id", it[0])
            map.put("label", it[0]+" : "+it[1])
            map.put("value", it[0])
            list.add(map) // add to the arraylist
        }

        return list
    }
}
