package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.enums.ModularDomainEntity

@Transactional
class ModuleService {

    Module get(Serializable id){
        Module.get(id)
    }

    List<Module> list(Map args){
        Module.list(args)
    }

    Long count(){
        Module.count()
    }

    void delete(Serializable id){
        get(id).delete()
    }

    Module save(Module module){
        module.save(flush:true)
    }

    List<Module> findAllByCodes(Collection<? extends String> codes){

        if (codes == null || codes.empty) return new ArrayList<Module>()

        Module.findAllByCodeInList(codes)
    }

    List<String> getListModulesFrom(String modules) {
        def spt = StringUtil.isBlank(modules) ? new String[0] : modules.split(",")

        def list = new ArrayList<String>()
        list.addAll(spt)

        return list
    }

    String getListModulesAsText(Collection<? extends String> listModules) {
        def str = "" as String
        listModules?.each {
            str = (str==null ? "":str+",") + it
        }

        return str
    }

    List<String> addDefaultModuleTo(List<String> modules) {
        def module = getDefaultModule()
        if (module != null) {
            //println "${modules}, ${module.code}"

            modules.add(module.code)
        }

        return modules
    }

    Module getDefaultModule() {
        def result = Module.executeQuery("select m from Module m order by m.code asc", [max: 1, offset: 0])
        if (result != null && !result.empty) {
            return result.first()
        }

        return null
    }

    /*
    * Return modules that dont exists
    * */
    String getNonExistenceModuleCodes(String modules){
        def list = new ArrayList<String>()

        getListModulesFrom(modules).each {
            if (Module.countByCode(it)==0){ //if module.code not found add to list
                list.add(it)
            }
        }

        return getListModulesAsText(list)
    }

    ArrayList<String[]> getGroupModulesMappings() {
        def groupModulesMappings = new ArrayList<String[]>()

        def region = new String[4];
        region[0] = ModularDomainEntity.REGION.name
        region[1] = Region.count() + ""
        region[2] = Region.countByModulesIsNotNull() + ""
        region[3] = (Region.count() - Region.countByModulesIsNotNull()) + ""

        def household = new String[4];
        household[0] = ModularDomainEntity.HOUSEHOLD.name
        household[1] = Household.count() + ""
        household[2] = Household.countByModulesIsNotNull() + ""
        household[3] = (Household.count() - Household.countByModulesIsNotNull()) + ""

        def member = new String[4];
        member[0] = ModularDomainEntity.MEMBER.name
        member[1] = Member.count()-1 + ""
        member[2] = Member.countByModulesIsNotNull() + ""
        member[3] = (Member.count()-1 - Member.countByModulesIsNotNull()) + ""


        groupModulesMappings.add(region)
        groupModulesMappings.add(household)
        groupModulesMappings.add(member)
        //TOTAL records, records w modules, records wo modules

        return groupModulesMappings
    }

    int[] getEntitiesCodesCounting(String filename) {
        def counts = new int[3]

        def list = getEntitiesCodesList(filename)

        counts[0] = list.get(0).size()
        counts[1] = list.get(1).size()
        counts[2] = list.get(2).size()

        return counts
    }

    List<List<String>> getEntitiesCodesList(String filename) {
        def list = new ArrayList<List<String>>()

        list.add(new ArrayList<String>()) //regions
        list.add(new ArrayList<String>()) //households
        list.add(new ArrayList<String>()) //members


        File file = new File(filename)

        if (file.exists() && file.isFile()){
            file.eachLine { line ->
                if (line != null && !line.trim().isEmpty()){
                    def s = line.trim() //check if ID exits

                    if (s.length()==3) { //region code = TXU
                        list.get(0).add(s)
                    }

                    if (s.length()==9) { //household code = TXUPF1001
                        list.get(1).add(s)
                    }

                    if (s.length()==12) { //member code = TXUPF1001001
                        list.get(2).add(s)
                    }



                }
            }
        }

        return list
    }


}