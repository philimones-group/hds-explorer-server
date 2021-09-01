package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil

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

        return new ArrayList<String>(spt)
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
            modules.add(module.code)
        }
    }

    Module getDefaultModule() {
        def result = Module.executeQuery("select m from Module m order by m.code asc limit 1")
        if (!result?.empty) {
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


}