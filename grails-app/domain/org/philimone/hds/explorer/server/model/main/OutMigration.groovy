package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType

import java.time.LocalDate

class OutMigration extends CollectableEntity {

    String id
    /*
     * The individual that its moving to a new Household
     */
    Member member
    /*
     * The individual code that its moving to a new Household
     */
    String memberCode

    /*
     * Type of OutMigration (Internal - for in-dss movements, External for Exiting from outside)
     */
    OutMigrationType migrationType

    /*
    * The Household from where the member is outmigrating from
    */
    Household origin
    /*
    * The Household Code from where the member is outmigrating from
    */
    String originCode
    /*
     * The residency record of the member current household
     */
    Residency originResidency

    /*
     * The household where this member is going to live, it is a Internal OutMigration
     */
    Household destination
    /*
     * The household code where this member is going to live, it is a Internal OutMigration
     */
    String destinationCode
    /*
    * The description of the place where the member is outmigrating to, if it is a External OutMigration (out of study area)
    */
    String destinationOther

    /*
     * Date of the Migration
     */
    LocalDate migrationDate
    /*
     * Reason for InMigration
     */
    String migrationReason
    /*
     * Household Visit used to capture the OutMigration
     */
    Visit visit
    String visitCode


    static constraints = {
        id maxSize: 32
        member nullable: false
        memberCode blank: false, nullable: false

        migrationType nullable: false, blank: false, enumType: "string"

        origin nullable: false
        originCode nullable: false, blank: false
        originResidency nullable: false

        destination nullable: true
        destinationCode nullable: true, blank: true
        destinationOther nullable: true, blank: true

        migrationDate nullable: false
        migrationReason nullable: true, blank: true

        visit nullable: false
        visitCode blank: false, nullable: false
    }

    static mapping = {
        table 'out_migration'

        id column: "id", generator: 'uuid'

        member     column: "member_id"
        memberCode column: "member_code", index: "idx_member_code"

        migrationType       column: "type"

        origin          column: "origin_id"
        originCode      column: "origin_code", index: "idx_origin_code"
        originResidency column: "origin_residency"

        destination      column: "destination_id"
        destinationCode  column: "destination_code", index: "idx_destination_code"
        destinationOther column: "destination_other"

        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
    }
}
