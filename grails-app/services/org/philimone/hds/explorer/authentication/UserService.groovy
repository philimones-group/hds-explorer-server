package org.philimone.hds.explorer.authentication

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Transactional
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
}
