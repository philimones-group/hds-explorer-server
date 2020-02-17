package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Membership implements Serializable, XmlModel {

    private static final long serialVersionUID = 571090333555561853L;

    private String collectedBy;
    private String individual;
    private String socialGroup;
    private String startDate;
    private String startType;
    private String endDate;
    private String endType;
    private String bIsToA;

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public String getSocialGroup() {
        return socialGroup;
    }

    public void setSocialGroup(String socialGroup) {
        this.socialGroup = socialGroup;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(Date date) {
        try {
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            String sdate = outFormat.format(date);
            this.startDate = sdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }
        
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(Date date) {
        try {
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            String sdate = outFormat.format(date);
            this.endDate = sdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getbIsToA() {
        return bIsToA;
    }

    public void setbIsToA(String bIsToA) {
        this.bIsToA = bIsToA;
    }       
        
    @Override
    public String getXml() {
        return "<membership>\n"  +
                "    <collectedBy>\n" +
                "        <extId>"+collectedBy+"</extId>\n" +
                "    </collectedBy>\n" +
                "    <individual>\n" +
                "        <extId>"+individual+"</extId>\n" +
                "    </individual>\n" +
                "    <socialGroup>\n" +
                "        <extId>"+socialGroup+"</extId>\n" +
                "    </socialGroup>\n" +
                "    <startDate>"+startDate+"</startDate>\n" +
                "    <endDate>"+endDate+"</endDate>\n" +
                "    <startType>"+startType+"</startType>\n" +
                "    <endType>"+endType+"</endType>   \n" +
                "    <bIsToA>"+bIsToA+"</bIsToA>\n" +
                "</membership>";
    }
}

