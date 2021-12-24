package org.philimone.hds.explorer.server.openhds.xml.model;

import net.betainteractive.utilities.StringUtil;
import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.util.Date;

public class Visit implements Serializable, XmlModel {

    private static final long serialVersionUID = -1429712555458116315L;
    private String extId;
    private String location;
    private String date;
    private String round;
    private String collectedBy;
    private String intervieweeId;
    private String realVisit;

    public String getIntervieweeId() {
        return intervieweeId;
    }

    public void setIntervieweeId(String intervieweeId) {
        this.intervieweeId = intervieweeId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(Date date) {
        this.date = StringUtil.format(date, "yyyy-MM-dd");
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public void setRealVisit(String realVisit) {
        this.realVisit = realVisit
    }

    String getRealVisit() {
        return realVisit
    }

    @Override
    public String getXml() {
        return "<visit>\n" +
               "  <extId>"+extId+"</extId>\n" +
               "  <collectedBy>\n" +
               "    <extId>"+collectedBy+"</extId>\n" +
               "  </collectedBy>" +
               "  <roundNumber>"+round+"</roundNumber>\n" +
               "  <visitDate>"+date+"</visitDate>\n" +
               "  <visitLocation>\n" +
               "    <extId>"+location+"</extId>\n" +
               "  </visitLocation>\n" +
               "  <realVisit>"+realVisit+"</realVisit>" +
               "</visit>";
    }
}
