/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Paulo Filimone
 */
public class Residency implements Serializable, XmlModel {

    private String collectedBy;    
    private String startDate;
    private String startType;
    private String endDate;
    private String endType;
    private String individual;
    private String location;

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
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

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public String getIndividual() {
        return individual;
    }

    public void setIndividual(String individual) {
        this.individual = individual;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
            
    @Override
    public String getXml() {
        return "<residency>\n" +
               "    <collectedBy>\n" +
               "        <extId>"+collectedBy+"</extId>\n" +
               "    </collectedBy>\n" +
               "    <individual>\n" +
               "        <extId>"+individual+"</extId>\n" +
               "    </individual>\n" +
               "    <location>\n" +
               "        <extId>"+location+"</extId>\n" +
               "    </location>\n" +
               "    <startDate>"+startDate+"</startDate>\n" +
               "    <startType>"+startType+"</startType>\n" +
               "    <endDate>"+endDate+"</endDate>\n" +
               "    <endType>"+endType+"</endType>\n" +
               "</residency>";
    }

}
