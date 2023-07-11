package org.philimone.hds.explorer.server.model.audit

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User

import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
class CollectableEntityService {

    /*
     Use the Username
     */
    def setUserCollectedBy(CollectableEntity entity, String username, LocalDateTime collectedDate) {

        def user = User.findByUsername(username)

        entity.collectedBy = user
        entity.collectedDate = collectedDate
    }

    /*def setUserCollectedBy(CollectableEntity entity, String username, String collectedDate) {
        setUserCollectedBy(entity, username, LocalDate.parse(collectedDate))
    }*/
}
