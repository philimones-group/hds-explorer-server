package org.philimone.hds.explorer.server.model.authentication


import grails.gorm.transactions.Transactional
import grails.web.mapping.LinkGenerator
import net.betainteractive.utilities.StringUtil
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.services.GeneralUtilitiesService
import org.philimone.hds.explorer.services.errors.ErrorMessageService

@Transactional
class UserService {

    def GeneralUtilitiesService generalUtilitiesService
    def ErrorMessageService errorMessageService
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

        user.save(flush: true)
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

        user.save(flush: true)
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

        user.save(flush: true)

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
        return pe.matches(newPassword, user.password)
    }

    ImportUsersResult importUsers(String xlsFilename, List<Role> roles, List<Module> modules) {
        def xlsFormInput = new FileInputStream(xlsFilename)
        def xlsFormWorkbook = new XSSFWorkbook(xlsFormInput)

        //get sheet lists and settings
        def xlsFormSheet = null as XSSFSheet

        try {
            xlsFormSheet = xlsFormWorkbook.getSheetAt(0)
        } catch(Exception ex) {
            ex.printStackTrace()

            def message = errorMessageService.getRawMessage("trackingList.validation.sheets.error")
            def result = new ImportUsersResult(ValidationStatus.ERROR, [message])

            return result
        }

        def errorMessages = new ArrayList<RawMessage>()
        def createdUsers = new ArrayList<User>()

        //read file
        xlsFormSheet.rowIterator().eachWithIndex { row, index ->
            //skip header
            if (index == 0) return

            String firstName = getStringCellValue(row.getCell(1))
            String lastName = getStringCellValue(row.getCell(2))
            String password = getStringCellValue(row.getCell(4))

            if (StringUtil.isBlank(firstName) || StringUtil.isBlank(lastName) || StringUtil.isBlank(password)) return

            User user = new User(code: "", username: "", firstName: firstName, lastName: lastName)
            user.code = codeGeneratorService.generateUserCode(user)
            user.username = "FW${user.code}"
            user.password = password

            println "${firstName},${lastName},${user.username},${getStringCellValue(row.getCell(4))}"

            //save users
            user.save(flush: true)

            if (user.hasErrors()) {
                println "user errors: ${user.errors}"
                errorMessages.addAll(errorMessageService.getRawMessages(user))
                return
            }

            UserRole.create(user, roles, true)

            def module = moduleService.getDefaultModule()
            if (modules?.empty && module!=null){
                modules.add(module)
            }

            modules.each {
                user.addToModules(it.code)
            }
            user.save()

            createdUsers.add(user)
        }

        //ends if there is an error
        if (errorMessages.size()>0) {
            return new ImportUsersResult(ValidationStatus.ERROR, errorMessages)
        }

        def result = new ImportUsersResult(ValidationStatus.SUCCESS, errorMessages)
        result.createdUsers.addAll(createdUsers)

        return result
    }

    String getStringCellValue(Cell cell){
        DataFormatter df = new DataFormatter();
        String value = df.formatCellValue(cell);
        return value==null ? "" : value
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

    class ImportUsersResult {
        ValidationStatus status
        List<RawMessage> errorMessages = new ArrayList<>()
        List<User> createdUsers = new ArrayList<>()

        ImportUsersResult(ValidationStatus status, List<RawMessage> errors) {
            this.status = status
            this.errorMessages.addAll(errors)
        }
    }

    enum ValidationStatus { SUCCESS, ERROR }
}