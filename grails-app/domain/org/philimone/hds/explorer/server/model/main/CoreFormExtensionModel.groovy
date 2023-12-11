package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.extensions.DatabaseColumnType
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType

class CoreFormExtensionModel {

    String id
    CoreFormExtension coreForm
    String  extFormId
    Integer dbColumnIndex
    String  dbColumnTable
    String  dbColumnName
    DatabaseColumnType dbColumnType
    Integer dbColumnSize

    String  formColumnName
    FormColumnType formColumnType

    String  formRepeatGroup
    Integer formRepeatLength //The total of possible iteration for a repeat group, in the db repeats are represented in wide format

    Boolean formChoiceList
    String  formChoiceValue

    Boolean repeatPerTable = false

    CoreFormExtensionModel parentGroup

    static constraints = {
        id maxSize: 32

        coreForm nullable: false, unique: ['dbColumnTable', 'dbColumnName'] //RECREATE DATABASE
        extFormId nullable: false, blank: false
        dbColumnIndex nullable: true, min: 0
        dbColumnTable nullable: false, blank: false
        dbColumnName nullable: false, blank: false
        dbColumnType nullable: true
        dbColumnSize nullable: true
        formColumnName nullable: false, blank: false
        formColumnType nullable: false, blank: false

        formRepeatGroup nullable: true
        formRepeatLength nullable: true

        formChoiceList nullable: true
        formChoiceValue nullable: true

        repeatPerTable nullable: false

        parentGroup nullable: true
    }

    static mapping = {
        table 'core_form_extension_model'

        id column: "id", generator: 'uuid'
        coreForm column: 'core_form_id'

        extFormId column: 'ext_form_id'

        dbColumnIndex column: 'db_column_index'
        dbColumnTable column: 'db_column_table'
        dbColumnName column: 'db_column_name'
        dbColumnType column: 'db_column_type', enumType: "identity"
        dbColumnSize column: 'db_column_size'

        formColumnName column: 'form_column_name'
        formColumnType column: 'form_column_type', enumType: "identity"

        formRepeatGroup column: 'form_repeat_group'
        formRepeatLength column: 'form_repeat_length'

        formChoiceList column: 'form_choidce_list'
        formChoiceValue column: 'form_choice_value'

        repeatPerTable column: 'repeat_per_table'

        parentGroup column: 'parent_group_id'

    }

}
