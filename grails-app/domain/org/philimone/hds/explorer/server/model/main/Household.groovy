package org.philimone.hds.explorer.server.model.main

import net.betainteractive.utilities.GeneralUtil
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.types.StringCollectionType

/**
 * Represents an distinct Household in the system that belongs to a administrative division (region or hieararchy)
 */
class Household extends CollectableEntity {

    String id
    String code
    String region;      /* Last Location Hierarchy */
    String name
    String headCode;    /* Head of Houeshold Code*/
    String headName     /* Head of Household Name*/
    String secHeadCode; /* Secondary Head of Household */

    Region parentRegion
    Member headMember

    String hierarchy1
    String hierarchy2
    String hierarchy3
    String hierarchy4
    String hierarchy5
    String hierarchy6
    String hierarchy7
    String hierarchy8

    Boolean gpsNull = true
    Double gpsAccuracy
    Double gpsAltitude
    Double gpsLatitude
    Double gpsLongitude

    Double cosLatitude
    Double sinLatitude
    Double cosLongitude
    Double sinLongitude

    HouseholdStatus status

    Boolean preRegistered = false

    static hasMany = [modules:String]

    static constraints = {
        id maxSize: 32
        code unique: true
        region nullable: false
        name blank: false
        headCode  blank: true, nullable: true, unique: 'code'
        headName blank: true, nullable: true
        secHeadCode blank: true, nullable: true, index: "idx_sec_head_code"

        parentRegion nullable: false
        headMember nullable: true

        hierarchy1 nullable: true
        hierarchy2 nullable: true
        hierarchy3 nullable: true
        hierarchy4 nullable: true
        hierarchy5 nullable: true
        hierarchy6 nullable: true
        hierarchy7 nullable: true
        hierarchy8 nullable: true

        gpsNull nullable: false
        gpsAccuracy nullable: true
        gpsAltitude nullable: true
        gpsLatitude nullable: true
        gpsLongitude nullable: true

        cosLatitude nullable: true
        sinLatitude nullable: true
        cosLongitude nullable: true
        sinLongitude nullable: true

        status nullable: true

        preRegistered nullable: false

        modules nullable: true, index: "idx_modules"
    }

    static mapping = {
        table 'household'

        id column: "id", generator: 'uuid'

        code column: 'code'
        region column: 'region'
        name column: 'name'
        headCode column: 'head_code'
        headName column: 'head_name'
        secHeadCode column: 'sec_head_code'

        parentRegion column: 'region_id'
        headMember column: 'head_id'

        hierarchy1 column: 'hierarchy1'
        hierarchy2 column: 'hierarchy2'
        hierarchy3 column: 'hierarchy3'
        hierarchy4 column: 'hierarchy4'
        hierarchy5 column: 'hierarchy5'
        hierarchy6 column: 'hierarchy6'
        hierarchy7 column: 'hierarchy7'
        hierarchy8 column: 'hierarchy8'

        gpsNull column: 'gps_is_null'
        gpsAccuracy column: 'gps_accuracy'
        gpsAltitude column: 'gps_altitude'
        gpsLatitude column: 'gps_latitude'
        gpsLongitude column: 'gps_longitude'

        cosLatitude column: 'cos_latitude'
        sinLatitude column: 'sin_latitude'
        cosLongitude column: 'cos_longitude'
        sinLongitude column: 'sin_longitude'

        status column: 'status'

        preRegistered column: 'pre_registered'

        modules column: "modules", type: StringCollectionType
    }

    def static ALL_COLUMNS = ['code', 'region', 'name', 'headCode', 'headName', 'secHeadCode', 'hierarchy1', 'hierarchy2', 'hierarchy3', 'hierarchy4', 'hierarchy5', 'hierarchy6', 'hierarchy7', 'hierarchy8', 'gpsAccuracy', 'gpsAltitude', 'gpsLatitude', 'gpsLongitude', 'collectedId']
}
