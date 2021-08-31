package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional

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

    String getStringListModules(Collection<? extends String> listModules) {
        def str = "" as String
        listModules.each {
            str = (str==null ? "":str+",") + it
        }

        return str
    }

    Module getDefaultModule() {
        def result = Module.executeQuery("select m from Module m order by m.code asc limit 1")
        if (!result?.empty) {
            return result.first()
        }

        return null
    }
}