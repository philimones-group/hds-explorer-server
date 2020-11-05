package org.philimone.hds.explorer.server.model.settings

class Codes {

    static final String PARAMS_TRACKLIST_MAX_DATA_COLUMNS = "hds.explorer.trackinglists.max_data_columns"
    static final String PARAMS_GENDER_CHECKING = "hds.explorer.constraints.gender.checking"
    static final String PARAMS_MIN_AGE_OF_FATHER = "hds.explorer.constraints.father.age.min"
    static final String PARAMS_MIN_AGE_OF_MOTHER = "hds.explorer.constraints.mother.age.min"
    static final String PARAMS_MIN_AGE_OF_HEAD   = "hds.explorer.constraints.head.age.min"

    static final String REGION_CODE_PATTERN = '^[A-Z0-9]{3}$'
    static final String HOUSEHOLD_CODE_PATTERN = '^[A-Z0-9]{6}[0-9]{3}$'
    static final String MEMBER_CODE_PATTERN = '^[A-Z0-9]{6}[0-9]{6}$'

    /* Mutable Constants - Will be loaded during bootstrap */
    static int MAX_TRACKLIST_DATA_COLUMNS_VALUE = 20
    static int MIN_MOTHER_AGE_VALUE = 12
    static int MIN_FATHER_AGE_VALUE = 12
    static int MIN_HEAD_AGE_VALUE = 12
    static boolean GENDER_CHECKING = true
}
