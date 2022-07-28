package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.FormCollectType

class FormGroupMapping  {

    String id
    Form groupForm
    String groupFormId
    int ordinal
    Form form
    String formId
    boolean formRequired = true
    FormCollectType formCollectType = FormCollectType.NORMAL_COLLECT
    String formCollectCondition
    String formCollectLabel

    static belongsTo = [groupForm:Form]

    static constraints = {

        id maxSize: 32

        groupForm nullable: false
        groupFormId nullable: false
        ordinal min: 0
        form nullable: false
        formId blank: false, nullable: false
        formRequired nullable: false
        formCollectType nullable: false
        formCollectCondition nullable: true
        formCollectLabel nullable: true
    }

    static mapping = {
        table "form_group_mapping"

        id column: "id", generator: 'uuid'

        groupForm column: "group_form_uuid"
        groupFormId column: "group_form_id"
        ordinal column: "ordinal"
        form column: "form_uuid"
        formId column: "form_id"
        formRequired column: "form_required"
        formCollectType column: "form_collect_type", enumType: "identity"
        formCollectCondition column: "form_collect_condition"
        formCollectLabel column: "form_collect_label"
    }


    def static ALL_COLUMNS = ['form_group_code', 'form_group_uuid']
}
