package org.philimone.hds.explorer.authentication

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class UserController {

    UserService userService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        respond userService.list(), model:[userCount: userService.count()] //userService.list(params), [userList: userService.list(params)]
    }

    def show(Long id) {
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

        try {
            userService.addUser(user)
            UserRole.create(user, userRoles, true)
        } catch (ValidationException e) {
            respond user.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect controler:"user", action: 'show', id: user.id
            }
            '*' { respond user, [status: CREATED] }
        }
    }

    def edit(Long id) {
        def user = userService.get(id)
        def userRoles = user.authorities.asList()

        respond user, model: [userRoles: userRoles]
    }

    def changePassword(Long id){
        respond userService.get(id)
    }

    def update(User user) {
        if (user == null) {
            notFound()
            return
        }

        def userRoles = Role.getAll(params.list("roles.id")) //selected roles

        try {
            userService.updateUser(user)
        } catch (ValidationException e) {
            respond user.errors, view:'edit', model: [userRoles: userRoles]
            return
        }

        println "${userRoles}"

        //update user roles
        UserRole.removeAll(user)
        UserRole.create(user, userRoles, true)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
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

    def delete(Long id) {
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
