package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType

import java.time.LocalDate

class InMigration extends CollectableEntity {

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
     * Type of InMigration (Internal - for in-dss movements, External for Entrance from outside)
     */
    InMigrationType type

    /*
     * External InMigration Type - ENTRY OR REENTRY
     */
    ExternalInMigrationType extMigType

    /*
    * The Household from where the member comes from if it is a Internal InMigration
    */
    Household origin
    /*
    * The Household Code from where the member comes from if it is a Internal InMigration
    */
    String originCode
    /*
    * The description of the place where the member comes from if it is a External InMigration
    */
    String originOther

    /*
     * The household this member is inmigrating to
     */
    Household destination
    /*
     * The household code this member is inmigrating to
     */
    String destinationCode
    /**
     * Residency record where the member is inmigrating to
     */
    Residency destinationResidency

    /*
     * Date of the Migration
     */
    LocalDate migrationDate
    /*
     * Reason for InMigration
     */
    String migrationReason

    String education
    String religion
    String phonePrimary
    String phoneAlternative

    /*
     * Household Visit used to capture the InMigration
     */
    Visit visit
    String visitCode


    static constraints = {
        id maxSize: 32
        member nullable: false
        memberCode blank: false, nullable: false

        type nullable: false, blank: false
        extMigType nullable: true, blank: true

        origin nullable: true
        originCode nullable: true, blank: true
        originOther nullable: true, blank: true

        destination nullable: false
        destinationCode nullable: false, blank: false
        destinationResidency nullable: true

        migrationDate nullable: false
        migrationReason nullable: true, blank: true

        education nullable: true
        religion nullable: true
        phonePrimary blank: true, nullable: true
        phoneAlternative blank: true, nullable: true

        visit nullable: false
        visitCode blank: false, nullable: false
    }

    static mapping = {
        table 'in_migration'

        id column: "id", generator: 'uuid'

        member     column: "member_id"
        memberCode column: "member_code", index: "idx_member_code"

        type       column: "type", enumType: "identity"
        extMigType column: "ext_migtype", enumType: "string"

        origin      column: "origin_id"
        originCode  column: "origin_code", index: "idx_origin_code"
        originOther column: "origin_other"

        destination          column: "destination_id"
        destinationCode      column: "destination_code", index: "idx_destination_code"
        destinationResidency column: "destination_residency"

        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"

        education column: "education"
        religion column: "religion"
        phonePrimary column: "phone_primary"
        phoneAlternative column: "phone_alternative"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
    }
}
