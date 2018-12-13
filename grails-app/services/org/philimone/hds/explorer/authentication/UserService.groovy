package org.philimone.hds.explorer.authentication

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Service(User)
class UserService {

    def GeneralUtilitiesService generalUtilitiesService

    def User addUser(User user) {
        //timestamp
        def username = user.username
        def password = user.password

        user.createdBy = generalUtilitiesService.currentUser()
        user.creationDate = new Date()

        def result = user.save(flush: true)

        //test send emails
        def svc = generalUtilitiesService

        svc.sendTextEmail(user.email,
                svc.getMessage("default.mail.user.subject.created", ""),
                svc.getMessage("default.mail.user.message.created", [ username, password ] as String[] , "") )

        //println "error ${result.errors}"
        result
    }

    def User updateUser(User user){
        //timestamp
        user.updatedBy = generalUtilitiesService.currentUser()
        user.updatedDate = new Date()

        user.save(flush: true)

        //println "error ${user.errors}"
    }

    User get(Serializable id){
        User.get(id)
    }

    List<User> list(Map args){
        User.list(args)
    }

    Long count(){
        User.count()
    }

    void delete(Serializable id){
        User.get(id).delete()
    }


}