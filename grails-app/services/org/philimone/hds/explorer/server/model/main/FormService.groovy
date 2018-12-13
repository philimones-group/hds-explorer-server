package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional

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

    Form save(Form form){
        form.save(flush:true)
    }
}
