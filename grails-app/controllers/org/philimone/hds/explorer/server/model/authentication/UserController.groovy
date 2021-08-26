package org.philimone.hds.explorer.server.model.authentication

import grails.validation.ValidationException
import org.philimone.hds.explorer.server.model.main.Module

import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        respond userService.list(), model:[userCount: userService.count()] //userService.list(params), [userList: userService.list(params)]
    }

    def show(String id) {
        respond userService.get(id)
    }

    def create() {
        respond new User(params)
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
        def userModules = user.modules.asList()

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
