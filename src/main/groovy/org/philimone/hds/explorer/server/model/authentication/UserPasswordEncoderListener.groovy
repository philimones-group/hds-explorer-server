package org.philimone.hds.explorer.server.model.authentication

import grails.plugin.springsecurity.SpringSecurityService
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.beans.factory.annotation.Autowired
import grails.events.annotation.gorm.Listener
import groovy.transform.CompileStatic

@CompileStatic
class UserPasswordEncoderListener {

    @Autowired
    SpringSecurityService springSecurityService

    @Listener(User)
    void onPreInsertEvent(PreInsertEvent event) {
        encodePasswordForEvent(event)
    }

    @Listener(User)
    void onPreUpdateEvent(PreUpdateEvent event) {
        encodePasswordForEvent(event)
    }

    private void encodePasswordForEvent(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof User) {
            User u = event.entityObject as User
            if (u.password && u.isPasswordEncoded==false && ( (event instanceof PreInsertEvent) || (event instanceof PreUpdateEvent && u.isDirty('password')))) { // do not encode password with isPasswordEncoded=true - they are imported from OpenHDS
                event.getEntityAccess().setProperty('password', encodePassword(u.password))
            }
        }
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password  //{bcrypt} - no more salt 8 the same used on openhds fieldworkers
    }
}
