package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.*
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PregnancyVisit

class RawPregnancyVisitChild {

    String id
    String pregnancyCode
    String outcomeType
    String childCode
    String childStatus                    //What is the current status of the child?  ALIVE, STILLBORN, DIED_AFTER_BIRTH
    Double childWeight                       //What is the weight of the child now? (kg)	decimal
    Boolean hadIllnessSymptoms //Has the child experienced any illness symptoms recently?
    String childIllnessSymptoms //select_multiple (fever, diarrhea, cough, rash, other)
    String childBreastfeedingStatus //Is the child currently being breastfed?	select_one (EXCLUSIVE, PARTIAL, NOT)
    String childImmunizationStatus   //Is the child up-to-date on immunizations?	select_one (YES, NO, UNKNOWN)
    String notes	                             //Additional observations	text

    @Override
    String toString() {
        "${childCode}"
    }

    static belongsTo = [pregnancyVisit:RawPregnancyVisit]

    static constraints = {
        id maxSize: 32

        pregnancyVisit nullable: false
        pregnancyCode nullable: false, blank: false
        outcomeType nullable: false
        childCode nullable: true, blank: true
        childStatus nullable: false

        childWeight nullable: true
        hadIllnessSymptoms nullable: true
        childIllnessSymptoms nullable: true
        childBreastfeedingStatus nullable: true
        childImmunizationStatus nullable: true
        notes nullable: true, maxSize: 1000
    }

    static mapping = {
        table '_raw_pregnancy_visit_child'

        id column: "id", generator: 'uuid'

        pregnancyVisit column: "pregnancy_visit_id"
        pregnancyCode column: "pregnancy_code", index: "idx_preg_code"
        outcomeType column: "outcome_type", index: "idx_outcome_type"
        childCode column: "child_code", index: "idx_child_code"
        childStatus column: "child_status", index: "idx_child_status"
        childWeight column: "child_weight"
        hadIllnessSymptoms column: "had_illness_symptoms"
        childIllnessSymptoms column: "child_illness_symptoms"
        childBreastfeedingStatus column: "child_breastfeeding_status"
        childImmunizationStatus column: "child_immunization_status"
        notes column: "notes"
    }

}
