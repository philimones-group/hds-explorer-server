package org.philimone.hds.explorer.server.openhds.xml

import org.philimone.hds.explorer.odk.model.CensusHouseholdCore
import org.philimone.hds.explorer.odk.model.CensusMemberCore
import org.philimone.hds.explorer.server.openhds.xml.model.*

class CensusData {
    String key

    Location locationXml
    Visit visitXml
    Baseline baselineXml
    SocialGroup socialGroupXml
    Membership membershipXml
    Relationship relationshipXml

    CensusHouseholdCore household
    CensusMemberCore member

    CensusData(){

    }

    CensusData(String keys){
        this.key = keys
    }

    static CensusData createData(String key){
        def cd = new CensusData(key)
        return cd
    }

    @Override
    boolean equals(Object obj) {
        CensusData cd = (CensusData) obj

        return key.equals(cd.key)
    }

    @Override
    int hashCode() {
        return Objects.hash(key)
    }
}
