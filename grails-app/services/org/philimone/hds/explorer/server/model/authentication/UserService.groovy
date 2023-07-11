package org.philimone.hds.explorer.server.model.authentication


import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Transactional
class UserService {

    def GeneralUtilitiesService generalUtilitiesService
    def codeGeneratorService
    def springSecurityService
    def moduleService

    LinkGenerator grailsLinkGenerator

    boolean exists(String username) {
        User.countByUsernameOrCode(username, username) > 0
    }

    boolean existsByCode(String code) {
        User.countByCode(code) > 0
    }

    User getUser(String username){
        if (!StringUtil.isBlank(username)) {
            return User.findByUsernameOrCode(username, username)
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
        user.code = codeGeneratorService.generateUserCode(user)

        user = user.save(flush: true)
        UserRole.create(user, roles, true)

        def module = moduleService.getDefaultModule()

        if (user?.modules?.empty && module!=null){
            user.addToModules(module.code)
        }

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

    User addUser(User user, List<Role> roles, List<Module> modules) {

        //generate user code
        user.code = codeGeneratorService.generateUserCode(user)

        user = user.save(flush: true)
        UserRole.create(user, roles, true)

        def module = moduleService.getDefaultModule()

        if (modules?.empty && module!=null){
            modules.add(module)
        }

        modules.each {
            user.addToModules(it.code)
        }

        println "modules: ${modules}"

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

    User updateUser(User user, List<Role> userRoles, List<Module> userModules) {
        println "${userRoles}"

        println "modules: ${userModules}"

        user.save(flush:true)

        //update user roles
        removeAllRoles(user)
        removeAllModules(user)

        UserRole.create(user, userRoles, true)

        userModules.each {
            user.addToModules(it.code)
        }

        user
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

            generalUtilitiesService.sendTextEmail(user.email, subject, message)
        }

        user
    }

    boolean passwordEquals(User user, String newPassword){
        def pe = springSecurityService.passwordEncoder
        return pe.isPasswordValid(user.password, newPassword, null)
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

    def removeAllRoles(User u) {
        UserRole.where { user == u }.deleteAll()
    }

    def removeAllRoles(Role r) {
        UserRole.where { role == r }.deleteAll()
    }

    def removeAllModules(User u){
        def modules = u.modules.collect { it }
        modules.each {
            u.removeFromModules(it)
        }
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