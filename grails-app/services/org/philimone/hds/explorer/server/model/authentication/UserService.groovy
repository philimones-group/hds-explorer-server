package org.philimone.hds.explorer.server.model.authentication

import grails.gorm.services.Service
import grails.web.mapping.LinkGenerator
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

        def result = user.save(flush: true)

        //test send emails
        def svc = generalUtilitiesService

        def url = grailsLinkGenerator.serverBaseURL

        svc.sendTextEmail(user.email,
                svc.getMessage("default.mail.user.subject.created", ""),
                svc.getMessage("default.mail.user.message.updated_password", [ url, username, password ] as String[] , "") )

        println "error ${result.errors}"
        result
    }

    def User updateUser(User user){

        user.save(flush: true)
        //println "error ${user.errors}"
    }

    def User updatePassword(User user, String newPassword){

        //timestamp
        user.password = newPassword

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

        //remove all the roles first - we dont delete users, just lock and disable

        def user = User.get(id)
        user.accountLocked = true
        user.accountLocked = true
        user.enabled = true

        //User.get(id).delete()

        user.save(flush: true)

    }


}