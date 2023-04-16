package org.philimone.utilities.grails

import grails.validation.Constrained
import org.grails.core.io.support.GrailsFactoriesLoader
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.validation.discovery.ConstrainedDiscovery
import org.springframework.util.Assert

/**
 * Comparator that uses the domain class property constraints to establish order in sort methods and always
 *  * places the id first.
 *  *
 *  * @author Paulo Filimone, but inspired from Graeme Rocher - DomainClassPropertyComparator.java
 */
class PersistentPropertyComparator implements Comparator {

    private Map constrainedProperties;
    private PersistentEntity domainClass;

    PersistentPropertyComparator(PersistentEntity domainClass) {
        Assert.notNull(domainClass, "Argument 'domainClass' is required!");

        this.domainClass = domainClass

        ConstrainedDiscovery constrainedDiscovery = GrailsFactoriesLoader.loadFactory(ConstrainedDiscovery.class);
        constrainedProperties = constrainedDiscovery.findConstrainedProperties(domainClass);
    }

    @Override
    int compare(Object o1, Object o2) {

        if (o1.equals(domainClass.getIdentity())) {
            return -1;
        }
        if (o2.equals(domainClass.getIdentity())) {
            return 1;
        }

        PersistentProperty prop1 = (PersistentProperty) o1;
        PersistentProperty prop2 = (PersistentProperty) o2;

        Constrained cp1 = (Constrained) constrainedProperties.get(prop1.name);
        Constrained cp2 = (Constrained) constrainedProperties.get(prop2.name);

        if (cp1 == null & cp2 == null) {
            return prop1.getName().compareTo(prop2.getName());
        }

        if (cp1 == null) {
            return 1;
        }

        if (cp2 == null) {
            return -1;
        }

        if (cp1.getOrder() > cp2.getOrder()) {
            return 1;
        }

        if (cp1.getOrder() < cp2.getOrder()) {
            return -1;
        }

        return 0;
    }
}
