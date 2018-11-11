package org.philimone.hds.explorer.authentication

import grails.gorm.transactions.Transactional

@Transactional
class UserService {

    def generalUtilitiesService

    def User addUser(User user) {
        //timestamp
        user.createdBy = generalUtilitiesService.currentUser()
        user.creationDate = new Date()

        user.save(flush: true)
    }

    def User updateUser(User user){
        //timestamp
        user.updatedBy = generalUtilitiesService.currentUser()
        user.updatedDate = new Date()

        user.save(flush: true)
    }
}
