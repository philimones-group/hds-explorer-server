package org.philimone.hds.explorer.server.model.audit

import grails.events.annotation.gorm.Listener
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.philimone.hds.explorer.server.model.authentication.User
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
@CompileStatic
class UserAuditListener {

    @Autowired
    SpringSecurityService springSecurityService

    @Listener(AuditableEntity)
    void preUpdateEvent(PreUpdateEvent event) {
        log.debug "Setting lastUpdatedBy [${event.entityObject}]"
        def user = getCurrentUser()

        event.entityAccess.setProperty('updatedBy', user)
        event.entityAccess.setProperty('updatedDate', new Date())
    }

    @Listener(AuditableEntity)
    void preInsertEvent(PreInsertEvent event) {
        AuditableEntity entity = (AuditableEntity) event.entityObject
        if (!entity.createdBy) {
            log.debug "Setting createdBy for instance [${entity}]"
            def user = this.getCurrentUser()

            event.entityAccess.setProperty('createdBy', user)
            event.entityAccess.setProperty('createdDate', new Date())
        }
    }

    @CompileDynamic
    User getCurrentUser() {
        springSecurityService.currentUser as User
    }
}
