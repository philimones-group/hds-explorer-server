databaseChangeLog = {
    include file: 'create-initial-tables.groovy'
    include file: 'add-preregistered-to-households.groovy'
    include file: 'remove-unique-constraints-on-rawdomains.groovy'
    include file: 'add-unique-ids-variables-to-raw-n-main-tables.groovy'
    include file: 'add-unique-flag-to-collected_id.groovy'
    include file: 'add-start-n-end-variables-to-domains.groovy'
    include file: 'alter-enumeration-event_date-dbcolumn_name.groovy'
    include file: 'add-education-n-religiom-cols-to-domains.groovy'
    include file: 'alter-rawdomains-add-extensionform-blob.groovy'
    include file: 'alter-add-extension-tables.groovy'
    include file: 'add-ext_form_definition-to-core-form-extension.groovy'
    include file: 'add-table-core_form_ext_model.groovy'
    include file: 'update-household_ext-remove-constraints.groovy'
    include file: 'update-extension_tables-remove-versioning.groovy'
    include file: 'alter-nullable-extmigtype_on_in_imigration_ext.groovy'
    include file: 'alter-tablename-change_head_ext.groovy'
    include file: 'add-typedesc_to_raw_event_table.groovy'
    include file: 'add-id-autoincrement_to_raw_error_log.groovy'
    include file: 'add-household_code-to_raw_event.groovy'
    include file: 'add-columns_to_visit_household_member-20240409.groovy'
    include file: 'add-columns_to_marital_relationship-20240509.groovy'
    include file: 'alter-visit-change-maxsize.groovy'
    include file: 'alter-residency-headrelationship-add-status.groovy'
    include file: 'add-partially-disabled-table.groovy'
    include file: 'update-tables-bad-enum-codes-20240823.groovy'
    include file: 'alter-table-raw_event-add-collected_date.groovy'
    include file: 'update-table-rawchangehead-nullable-oldheadcode-20240911.groovy'
    include file: 'alter-tables-coreformcomluns-add-column-code-20240913.groovy'
    include file: 'add-region-head-tables.groovy'
    include file: 'add-change-region-head-extension-table.groovy'
    include file: 'alter-change-region-head-extension-table.groovy'
}
