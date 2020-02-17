package org.philimone.hds.explorer.server.openhds.xml.model;

import net.betainteractive.utilities.StringUtil;
import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.util.Date;

public class Relationship implements Serializable, XmlModel {

    private static final long serialVersionUID = -761650887262715820L;

    private String collectedBy;
    private String individualA;
    private String individualB;
    private String startDate;
    private String aIsToB;

    public String getIndividualA() {
        return individualA;
    }

    public void setIndividualA(String individualA) {
        this.individualA = individualA;
    }

    public String getIndividualB() {
        return individualB;
    }

    public void setIndividualB(String individualB) {
        this.individualB = individualB;
    }

    // dates come in from the web SERVICE_CLIPREDCAP in dd-MM-yyyy format
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(Date date){
        this.startDate = StringUtil.format(date, "yyyy-MM-dd");
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getbIsToA() {
        return aIsToB;
    }

    public void setbIsToA(String bIsToA) {
        this.aIsToB = bIsToA;
    }

    @Override
    public String getXml() {
        return "<relationship>\n" +
               "    <collectedBy>\n" +
               "        <extId>"+collectedBy+"</extId>\n" +
               "    </collectedBy>\n" +
               "    <individualA>\n" +
               "        <extId>"+individualA+"</extId>\n" +
               "    </individualA>\n" +
               "    <individualB>\n" +
               "        <extId>"+individualB+"</extId>\n" +
               "    </individualB>\n" +
               "    <startDate>"+startDate+"</startDate>\n" +
               "    <aIsToB>"+aIsToB+"</aIsToB>\n" +
               "</relationship>";
    }
}
