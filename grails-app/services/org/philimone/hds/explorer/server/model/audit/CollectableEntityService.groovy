package org.philimone.hds.explorer.server.model.audit

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User

@Transactional
class CollectableEntityService {

    /*
     Use the Username
     */
    def setUserCollectedBy(CollectableEntity entity, String username, Date collectedDate) {

        def user = User.findByUsername(username)

        entity.collectedBy = user
        entity.collectedDate = collectedDate
    }

    def setUserCollectedBy(CollectableEntity entity, String username, String collectedDate) {
        setUserCollectedBy(entity, username, StringUtil.toDate(collectedDate, "yyyy-MM-dd"))
    }
}
