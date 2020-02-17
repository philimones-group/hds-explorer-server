package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;

public class SocialGroup implements Serializable, XmlModel {

    private static final long serialVersionUID = 571090333555561853L;

    private String collectedBy;
    private String extId;
    private String groupName;
    private String groupHead;
    private String groupType;

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupHead() {
        return groupHead;
    }

    public void setGroupHead(String groupHead) {
        this.groupHead = groupHead;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }    
    
    @Override
    public String getXml() {
        return "<socialgroup>\n" +
               "    <collectedBy>\n" +
               "        <extId>"+collectedBy+"</extId>\n" +
               "    </collectedBy>\n" +
               "    <extId>"+extId+"</extId>\n" +
               "    <groupName>"+groupName+"</groupName>\n" +
               "    <groupHead>\n" +
               "        <extId>"+groupHead+"</extId>\n" +
               "    </groupHead>\n" +
               "    <groupType>"+groupType+"</groupType>\n" +
               "</socialgroup>";
    }
}
