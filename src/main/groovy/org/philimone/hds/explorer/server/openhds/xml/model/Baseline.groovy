package org.philimone.hds.explorer.server.openhds.xml.model;

import net.betainteractive.utilities.StringUtil;
import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.util.Date;

public class Baseline implements Serializable, XmlModel {

    private String collectedBy;
    private String visitId;
    
    private String extId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private String mother;
    private String father;
    private String endType;

    private String origin;
    private String reason;
    private String recordedDate;
    private String migType;

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setDob(Date dob) {
        this.dob = StringUtil.format(dob, "yyyy-MM-dd");
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(String recordedDate) {
        this.recordedDate = recordedDate;
    }

    public void setRecordedDate(Date recordedDate) {
        this.recordedDate = StringUtil.format(recordedDate, "yyyy-MM-dd");
    }

    public String getMigType() {
        return migType;
    }

    public void setMigType(String migType) {
        this.migType = migType;
    }

    @Override
    public String getXml() {
        return  "<inmigration>\n" +
                "    <collectedBy>\n" +
                "        <extId>"+collectedBy+"</extId>\n" +
                "    </collectedBy>\n" +
                "    <visit>\n" +
                "        <extId>"+visitId+"</extId>\n" +
                "    </visit>\n" +
                "    <individual>\n" +
                "        <extId>"+extId+"</extId>\n" +
                "        <firstName>"+firstName+"</firstName>\n" +
                "        <lastName>"+lastName+"</lastName>\n" +
                "        <gender>"+gender+"</gender>\n" +
                "        <dob>"+dob+"</dob>\n" +
                "        <dobAspect>1</dobAspect>\n" +
                "        <mother>\n" +
                "            <extId>"+mother+"</extId>\n" +
                "        </mother>\n" +
                "        <father>\n" +
                "            <extId>"+father+"</extId>\n" +
                "        </father>\n" +
                "    </individual>\n" +
                "    <origin>"+origin+"</origin>\n" +
                "    <reason>"+reason+"</reason>\n" +
                "    <recordedDate>"+recordedDate+"</recordedDate>\n" +
                "    <migType>"+migType+"</migType>\n" +
                "</inmigration>";
    }
}
