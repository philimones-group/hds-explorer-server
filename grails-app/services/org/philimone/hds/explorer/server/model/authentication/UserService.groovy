package org.philimone.hds.explorer.server.model.authentication

import grails.gorm.services.Service
import grails.web.mapping.LinkGenerator
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Service(User)
class UserService {

    def GeneralUtilitiesService generalUtilitiesService
    def springSecurityService

    LinkGenerator grailsLinkGenerator

    boolean exists(String username) {
        User.countByUsername(username) > 0
    }

    boolean existsByCode(String code) {
        User.countByCode(code) > 0
    }

    User getUser(String username){
        if (!StringUtil.isBlank(username)) {
            return User.findByUsername(username)
        }
        return null
    }

    User getUserByCode(String code){
        if (!StringUtil.isBlank(code)) {
            return User.findByCode(code)
        }
        return null
    }

    User addUser(User user, List<Role> roles) {

        //generate user code
        user.code = generateCode(user)

        user = user.save(flush: true)
        UserRole.create(user, roles, true)

        //send email
        if (!StringUtil.isBlank(user?.email) && !user?.email?.equals("youremail@domain.net")){ //send email with the credentials if mail address exists

            def url = grailsLinkGenerator.serverBaseURL
            def subject = generalUtilitiesService.getMessage("default.mail.user.subject.created", "")
            def message = generalUtilitiesService.getMessage("default.mail.user.message.updated_password", [ url, user.username, user.password ] as String[] , "")

            generalUtilitiesService.sendTextEmail(user.email, subject, message)
        }

        //println "error ${user.errors}"

        return user
    }

    User updateUser(User user){

        user.save(flush: true)
        //println "error ${user.errors}"
    }

    User updatePassword(User user, String newPassword){

        //timestamp
        user.password = newPassword

        user = user.save(flush: true)

        //send email
        if (!StringUtil.isBlank(user?.email)) { //send email with the credentials if mail address exists
            def url = grailsLinkGenerator.serverBaseURL
            def subject = generalUtilitiesService.getMessage("default.mail.user.subject.updated_password", "")
            def message = generalUtilitiesService.getMessage("default.mail.user.message.updated_password", [url, user.username, newPassword] as String[], "")

            svc.sendTextEmail(user.email, subject, message)
        }

        user
    }

    boolean passwordEquals(User user, String newPassword){

        def pe = springSecurityService.passwordEncoder

        return pe.isPasswordValid(user.password, newPassword, null)
    }

    String encodePassword(String password) {
        springSecurityService.encodePassword(password, "8")
    }

    String generateCode(User user){
        def regexFw = '^FW[A-Za-z]{3}$'

        if (user.username.matches(regexFw)){ //ohds fieldworker
            return user.username.toUpperCase().replaceAll("FW")
        }else {
            def codes = User.list().collect{ t -> t.code}

            def f = user.firstName.toUpperCase()
            def l = user.lastName.toUpperCase()
            def alist = f.chars.toList()
            def blist = l.chars.toList()
            def clist = ("1".."9") + (l.length()>1 ? l.substring(1).chars.toList() : []) + ("A".."Z")

            for (def a : alist){
                for (def b : blist){
                    for (def c : clist){
                        def test = "${a}${b}${c}" as String

                        if (!codes.contains(test)){
                            return test
                        }
                    }

                }
            }
        }

        return null
    }

    List<String> getAuthoritiesText(User user){
        def list = new ArrayList<String>()

        if (user != null){
            user.authorities.each {
                list << generalUtilitiesService.getMessage(it.name, "")
            }
        }

        return list
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