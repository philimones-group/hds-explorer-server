databaseChangeLog = {
    include file: 'create-initial-tables.groovy'
    include file: 'add-preregistered-to-households.groovy'
    include file: 'remove-unique-constraints-on-rawdomains.groovy'
    include file: 'add-unique-ids-variables-to-raw-n-main-tables.groovy'
}
