package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.json.JRegionLevel
import org.philimone.hds.explorer.server.model.settings.ApplicationParam

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
        FormMapping.executeUpdate("delete from FormMapping f where f.id=?", [id])
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


}
