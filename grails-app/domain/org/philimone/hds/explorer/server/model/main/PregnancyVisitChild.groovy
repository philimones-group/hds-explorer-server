package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.BreastFeedingStatus
import org.philimone.hds.explorer.server.model.enums.IllnessSymptoms
import org.philimone.hds.explorer.server.model.types.IllnessSymptomsUserType
import org.philimone.hds.explorer.server.model.enums.ImmunizationStatus
import org.philimone.hds.explorer.server.model.enums.NewBornStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType

class PregnancyVisitChild {

    String id
    PregnancyVisit pregnancyVisit
    String pregnancyCode
    PregnancyOutcomeType outcomeType
    Member child
    String childCode
    NewBornStatus childStatus                    //What is the current status of the child?  ALIVE, STILLBORN, DIED_AFTER_BIRTH
    Double childWeight                       //What is the weight of the child now? (kg)	decimal
    Boolean hadIllnessSymptoms
    //Set<IllnessSymptoms> childIllnessSymptoms         //Has the child experienced any illness symptoms recently?	select_multiple (fever, diarrhea, cough, rash, other)
    BreastFeedingStatus childBreastfeedingStatus //Is the child currently being breastfed?	select_one (EXCLUSIVE, PARTIAL, NOT)
    ImmunizationStatus childImmunizationStatus   //Is the child up-to-date on immunizations?	select_one (YES, NO, UNKNOWN)
    String notes	                             //Additional observations	text

    static belongsTo = [pregnancyVisit:PregnancyVisit]

    static hasMany = [childIllnessSymptoms:IllnessSymptoms]

    static constraints = {
        id maxSize: 32

        pregnancyVisit nullable: false
        pregnancyCode nullable: false, blank: false
        outcomeType nullable: false
        child nullable: true
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
        table 'pregnancy_visit_child'

        id column: "id", generator: 'uuid'

        pregnancyVisit column: "pregnancy_visit_id"
        pregnancyCode column: "pregnancy_code", index: "idx_preg_code"
        outcomeType column: "outcome_type", enumType: "identity"
        child column: "child_id"
        childCode column: "child_code", index: "idx_child_code"
        childStatus column: "child_status", enumType: "identity"
        childWeight column: "child_weight"
        hadIllnessSymptoms column: "had_illness_symptoms"
        childIllnessSymptoms column: "child_illness_symptoms", type: IllnessSymptomsUserType
        childBreastfeedingStatus column: "child_breastfeeding_status", enumType: "identity"
        childImmunizationStatus column: "child_immunization_status", enumType: "identity"
        notes column: "notes"
    }

}
