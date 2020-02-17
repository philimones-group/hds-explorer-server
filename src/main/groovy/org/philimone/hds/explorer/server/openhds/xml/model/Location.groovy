package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;

public class Location implements Serializable, XmlModel {

    private static final long serialVersionUID = -8462273781331229805L;

    private String collectedBy;
    private String extId;
    private String name;
    private String type;
    private String accuracy;
    private String altitude;
    private String latitude;
    private String longitude;
    private String locationLevel;

    // specific field for cross river
    private String head;

    private static Location location;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
        
    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }
        
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationLevel() {
        return locationLevel;
    }

    public void setLocationLevel(String hierarchy) {
        this.locationLevel = hierarchy;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public static Location emptyLocation() {
        if (location == null) {
            location = new Location();
            location.extId = "";
            location.head = "";
            location.locationLevel = "";
            location.latitude = "";
            location.longitude = "";
            location.name = "";
        }

        return location;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    @Override
    public String getXml() {
        return "<location>\n" +
               "    <extId>"+extId+"</extId>\n" +
               "    <collectedBy>\n" +
               "        <extId>"+collectedBy+"</extId>\n" +
               "    </collectedBy>\n" +
               "    <locationLevel>\n" +
               "        <extId>"+locationLevel+"</extId>\n" +
               "    </locationLevel>\n" +
               "    <locationName>"+name+"</locationName>\n" +
               "    <locationType>"+type+"</locationType>\n" +
               "    <accuracy>"+accuracy+"</accuracy>\n" +
               "    <altitude>"+altitude+"</altitude>\n" +
               "    <longitude>"+longitude+"</longitude>\n" +
               "    <latitude>"+latitude+"</latitude>\n" +
               "</location>";
    }
}
