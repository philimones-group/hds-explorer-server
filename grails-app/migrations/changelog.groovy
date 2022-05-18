databaseChangeLog = {
    include file: 'create-initial-tables.groovy'
    include file: 'fix-raw_marital_relationship-constraints.groovy'
    include file: 'add-parent_region_code_to_region.groovy'
    include file: 'add-event_order-to-rawevent.groovy'
}