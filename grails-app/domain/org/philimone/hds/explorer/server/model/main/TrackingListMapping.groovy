package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.SubjectType

class TrackingListMapping {

    TrackingListGroup group
    Integer listId
    String  listTitle
    String subjectCode
    SubjectType subjectType
    String subjectForms
    String subjectVisitCode
    String subjectVisitUuid

    static belongsTo = [group:TrackingListGroup]

    static constraints = {
        //listId unique: 'group'
        listTitle blank: false, nullable: false
        subjectCode nullable: false
        subjectForms nullable: false
        subjectVisitCode nullable: false
        subjectVisitUuid nullable: true
    }

    static mapping = {
        table 'tracking_list_mapping'

        group column: 'group_uuid'
        listId column: 'list_id'
        listTitle column: 'list_title'
        subjectCode column: 'subject_code'
        subjectType column: 'subject_type', enumType: "identity"
        subjectForms column: 'subject_forms'
        subjectVisitCode column: 'subject_visit_code'
        subjectVisitUuid column: 'subject_visit_uuid'
    }
}
