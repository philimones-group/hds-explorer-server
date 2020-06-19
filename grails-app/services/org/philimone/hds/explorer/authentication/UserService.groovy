package org.philimone.hds.explorer.authentication

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import org.grails.web.util.WebUtils
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Service(User)
class UserService {

    def GeneralUtilitiesService generalUtilitiesService
    def springSecurityService

    LinkGenerator grailsLinkGenerator

    def test(){
        //println grailsLinkGenerator.serverBaseURL
    }

    def User addUser(User user) {
        //timestamp
        def username = user.username
        def password = user.password

        user.createdBy = generalUtilitiesService.currentUser()
        user.creationDate = new Date()

        def result = user.save(flush: true)

        //test send emails
        def svc = generalUtilitiesService

        def url = grailsLinkGenerator.serverBaseURL

        svc.sendTextEmail(user.email,
                svc.getMessage("default.mail.user.subject.created", ""),
                svc.getMessage("default.mail.user.message.updated_password", [ url, username, password ] as String[] , "") )

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

    def User updatePassword(User user, String newPassword){

        //timestamp
        user.password = newPassword
        user.updatedBy = generalUtilitiesService.currentUser()
        user.updatedDate = new Date()

        def result = user.save(flush: true)

        //CONTINUE TOMORROW
        def svc = generalUtilitiesService

        def url = grailsLinkGenerator.serverBaseURL

        svc.sendTextEmail(user.email,
                svc.getMessage("default.mail.user.subject.updated_password", ""),
                svc.getMessage("default.mail.user.message.updated_password", [ url, user.username, newPassword ] as String[] , "") )


        return result
    }

    boolean passwordEquals(User user, String newPassword){

        def pe = springSecurityService.passwordEncoder

        return pe.isPasswordValid(user.password, newPassword, null)
    }

    String encodePassword(String password) {
        springSecurityService.encodePassword(password, "8")
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