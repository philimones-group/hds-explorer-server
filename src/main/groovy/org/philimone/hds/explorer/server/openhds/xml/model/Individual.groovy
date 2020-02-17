package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Individual implements Serializable, XmlModel {

    private static final long serialVersionUID = -799404570247633403L;

    private String collectedBy;
    private String extId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dob;
    private String mother;
    private String father;    
    private String endType;

    private List<SocialGroup> socialGroups;

    private static Individual individual;

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

    // dates come in from the web SERVICE_CLIPREDCAP in dd-MM-yyyy format but
    // they must be changed to yyyy-MM-dd for ODK Collect
    public void setDob(String dob) {
        try {
            DateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            String date = outFormat.format(inFormat.parse(dob));
            this.dob = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDob(Date dob) {
        try {
            DateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            String date = outFormat.format(dob);
            this.dob = date;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDobIn(String dob) {
        try {
            DateFormat outFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");

            String date = outFormat.format(inFormat.parse(dob));
            this.dob = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDobFromCollect(String dob) {
        try {
            DateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            String date = outFormat.format(outFormat.parse(dob));
            this.dob = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
    
    public List<SocialGroup> getSocialGroups() {
        return socialGroups;
    }

    public void setSocialGroups(List<SocialGroup> socialGroups) {
        this.socialGroups = socialGroups;
    }

    public String getFullName() {
        StringBuilder builder = new StringBuilder();
        if (lastName != null) {
            //builder.append(lastName);
        }

        if (firstName != null) {
            builder.insert(0, firstName);
        }

        return builder.toString();
    }

    public static Individual emptyIndividual() {
        if (individual == null) {
            individual = new Individual();
            //  individual.setDob("1900-01-01");
            individual.setExtId("");
            individual.setFirstName("");
            individual.setLastName("");
        }

        return individual;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEndType() {
        return endType;
    }

    public void setEndType(String endType) {
        this.endType = endType;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }   
    
    @Override
    public String getXml() {
        return "<individual>\n" +
               "    <collectedBy>\n" +
               "        <extId>"+collectedBy+"</extId>\n" +
               "    </collectedBy>\n" +
               "    <extId>"+extId+"</extId>\n" +
               "    <firstName>"+firstName+"</firstName>\n" +
               "    <middleName>"+middleName+"</middleName>\n" +
               "    <lastName>"+lastName+"</lastName>\n" +
               "    <gender>"+gender+"</gender>\n" +
               "    <dob>"+dob+"</dob>\n" +
               "    <dobAspect></dobAspect>\n" +
               "    <mother>\n" +
               "        <extId>"+mother+"</extId>\n" +
               "    </mother>\n" +
               "    <father>\n" +
               "        <extId>"+father+"</extId>\n" +
               "    </father>\n" +
               "</individual>";
    }
}
