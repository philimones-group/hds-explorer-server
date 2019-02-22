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

    List<JConstant> getSpecialConstants(){
        def list = []

        list << new JConstant(name: "Timestamp     : Date[Y-M-D H:M:S]", value: "Timestamp")
        list << new JConstant(name: "Household Members <repeat group>", value: "HouseholdMembers")
        list << new JConstant(name: "Member Exists : Boolean[true, false]]", value: "MemberExists")

        return list
    }
}
