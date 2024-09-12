package org.philimone.hds.explorer.server.model.authentication

import grails.validation.ValidationException
import net.betainteractive.utilities.GeneralUtil
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.ModuleService
import org.philimone.hds.explorer.server.model.main.TrackingList
import org.philimone.hds.explorer.server.model.main.TrackingListService

import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService
    ModuleService moduleService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)

        def list = userService.list(params)
        println "list users = ${list.size()}"
        respond list, model:[userCount: userService.count()] //userService.list(params), [userList: userService.list(params)]
    }

    def show(String id) {
        def user = userService.get(id)
        def modules= moduleService.findAllByCodes(user?.modules)
        respond user, model: [modules: modules]
    }

    def create() {
        respond new User(params)
    }

    def importusers() {
        respond new User(params), model: [userInstance: new User(params)]
    }

    def uploadUsersFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = File.createTempFile("userslist-web-${GeneralUtil.generateUUID()}", "xlsx") //SystemPath.externalDocsPath + File.separator + fileName

        file.transferTo(new File(newFile))

        //read xls file
        //validate lists first
        //def validationResult = trackingListService.validateXls(newFile.toString())

        /*
        if (validationResult.status == TrackingListService.ValidationStatus.ERROR) {
            params.filename = fileName
            def trackingListInstance = params.trackingListId ? TrackingList.get(params.trackingListId) : new TrackingList(params)
            render view: "add", model: [trackingListInstance: trackingListInstance, absoluteFilename: newFile, errorMessages: validationResult.errorMessages]
            return
        }*/

        def modules = Module.findAllByCodeInList(params.modules)
        def userInstance = new User(params)

        //println "upload: selected trackinglist: ${trackingListInstance}, ${trackingListInstance?.id}, ${newFile}"

        render view: "importusers", model: [absoluteFilename: newFile, uploadedFilename: fileName, userInstance: userInstance, modules: modules]
    }

    def saveUsers(User user) {
        //discard this user at the end its just being used to carrie some variables

        def userRoles = Role.getAll(params.list("roles.id"))
        //params.remove("roles.id")

        def userModules = Module.getAll(params.list("modules"))
        //params.remove("modules")

        //import users from xls file
        def usersCreated = 0

        def importResult = userService.importUsers(params.absoluteFilename, userRoles, userModules)

        if (importResult.status == UserService.ValidationStatus.SUCCESS) {
            usersCreated = importResult.createdUsers.size()
        } else if (importResult.status == UserService.ValidationStatus.PARTIAL) {
            usersCreated = importResult.createdUsers.size()
            flash.message = message(code: 'user.file.import.success.label', args: [usersCreated])
            
            render view:'importusers', model: [userInstance: user, absoluteFilename: params.absoluteFilename, errorMessages: importResult.errorMessages, modules: params.list("modules")]
            return

        } else {
            render view:'importusers', model: [userInstance: user, absoluteFilename: params.absoluteFilename, errorMessages: importResult.errorMessages, modules: params.list("modules")]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'user.file.import.success.label', args: [usersCreated])
                redirect controler:"user", action: 'index'
            }
            '*' { respond user, [status: CREATED] }
        }

    }

    def save(User user) {
        if (user == null) {
            notFound()
            return
        }

        def userRoles = Role.getAll(params.list("roles.id"))
        params.remove("roles.id")

        def userModules = Module.getAll(params.list("modules"))
        params.remove("modules")

        try {
            userService.addUser(user, userRoles, userModules)
        } catch (ValidationException e) {
            respond user.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.username])
                redirect controler:"user", action: 'show', id: user.id
            }
            '*' { respond user, [status: CREATED] }
        }
    }

    def edit(String id) {
        def user = userService.get(id)
        def userRoles = user.authorities.asList()
        def modules = user.modules
        def userModules = modules.empty ? new HashSet<String>() : Module.findAllByCodeInList(modules)

        respond user, model: [userRoles: userRoles, userModules: userModules]
    }

    def changePassword(String id){
        respond userService.get(id)
    }

    def update(User user) {
        if (user == null) {
            notFound()
            return
        }

        def userRoles = Role.getAll(params.list("roles.id")) //selected roles
        def userModules = Module.getAll(params.list("modules"))

        try {
            userService.updateUser(user, userRoles, userModules)
        } catch (ValidationException e) {
            respond user.errors, view:'edit', model: [userRoles: userRoles]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.username])
                redirect user
            }
            '*'{ respond user, [status: OK] }
        }
    }

    def updatePassword(User user){

        def newPassword = params.newPassword
        def confirmPassword = params.confirmPassword

        //check if password is equal to old password
        if (userService.passwordEquals(user, newPassword)){
            //same password

            flash.message = message(code: 'user.password.used.recently')
            respond user, view: "changePassword"
            return
        }

        //check if password 1 is equal to password 2
        if (!newPassword.equals(confirmPassword)){
            //not equal

            flash.message = message(code: 'user.passwords.dont.match')
            respond user, view: "changePassword"
            return
        }


        try {
            userService.updatePassword(user, newPassword)
        } catch (ValidationException e) {
            respond user.errors, view:'changePassword'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'user.password.updated.label')
                redirect user
            }
            '*'{ respond user, [status: OK] }
        }

    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        userService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
