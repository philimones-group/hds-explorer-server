package org.philimone.hds.explorer.server.openhds.xml.model;

import org.philimone.hds.explorer.server.openhds.xml.XmlModel;

import java.io.Serializable;

public class Round implements Serializable, XmlModel {

    private static final long serialVersionUID = -2367646883047152268L;

    private String roundNumber;
    private String startDate;
    private String endDate;
    private String remarks;

    private static Round round;

    public Round() {
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public static Round getEmptyRound() {
        if (round == null) {
            round = new Round();
            round.setRoundNumber("");
            round.setEndDate("");
            round.setStartDate("");
        }

        return round;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String getXml() {
        return "<round>\n" +
               "  <roundNumber>"+roundNumber+"</roundNumber>\n" +
               "  <remarks>"+remarks+"</remarks>\n" +
               "  <startDate>"+startDate+"</startDate>\n" +
               "  <endDate>"+endDate+"</endDate>\n" +
               "</round>";
    }
}
