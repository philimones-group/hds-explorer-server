package org.philimone.hds.explorer.server.openhds.xml.model

import org.philimone.hds.explorer.server.openhds.xml.XmlModel

class PregnancyObservation implements XmlModel {

    private String motherId;
    private String expectedDeliveryDate;
    private String recordedDate;
    private String collectedBy;
    private String visitId;

    String getMotherId() {
        return motherId
    }

    void setMotherId(String motherId) {
        this.motherId = motherId
    }

    String getExpectedDeliveryDate() {
        return expectedDeliveryDate
    }

    void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate
    }

    String getRecordedDate() {
        return recordedDate
    }

    void setRecordedDate(String recordedDate) {
        this.recordedDate = recordedDate
    }

    String getCollectedBy() {
        return collectedBy
    }

    void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy
    }

    String getVisitId() {
        return visitId
    }

    void setVisitId(String visitId) {
        this.visitId = visitId
    }

    @Override
    String getXml() {
        return "<pregnancyobservation>\n" +
               "  <mother>\n" +
               "    <extId>"+motherId+"</extId>\n" +
               "  </mother>\n" +
               "  <expectedDeliveryDate>"+expectedDeliveryDate+"</expectedDeliveryDate>\n" +
               "  <recordedDate>"+recordedDate+"</recordedDate>\n" +
               "  <collectedBy>\n" +
               "    <extId>"+collectedBy+"</extId>\n" +
               "  </collectedBy>\n" +
               "  <visit>\n" +
               "    <extId>"+visitId+"</extId>\n" +
               "  </visit>\n" +
               "</pregnancyobservation>"
    }
}
