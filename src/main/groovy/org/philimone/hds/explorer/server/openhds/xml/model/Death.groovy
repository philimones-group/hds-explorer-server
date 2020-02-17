/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;

/**
 *
 * @author root
 */
public class Death implements Serializable, XmlModel {

    private String visitDeath;
    private String individual;
    private String deathPlace;
    private String deathCause;
    private String deathDate;
    private String collectedBy;

    public String getVisitDeath() {
        return visitDeath;
    }

    public void setVisitDeath(String visitDeath) {
        this.visitDeath = visitDeath;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public String getDeathPlace() {
        return deathPlace;
    }

    public void setDeathPlace(String deathPlace) {
        this.deathPlace = deathPlace;
    }

    public String getDeathCause() {
        return deathCause;
    }

    public void setDeathCause(String deathCause) {
        this.deathCause = deathCause;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }
            
    @Override
    public String getXml() {
        return "<death>\n" +
               "  <visitDeath>\n" +
               "    <extId>"+visitDeath+"</extId>\n" +
               "  </visitDeath>\n" +
               "  <individual>\n" +
               "    <extId>"+individual+"</extId>\n" +
               "  </individual>\n" +
               "  <deathPlace>"+deathPlace+"</deathPlace>\n" +
               "  <deathCause>"+deathCause+"</deathCause>\n" +
               "  <deathDate>"+deathDate+"</deathDate>\n" +
               "  <collectedBy>\n" +
               "    <extId>"+collectedBy+"</extId>\n" +
               "  </collectedBy>\n" +
               "</death>";
    }
    
}
