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
}
