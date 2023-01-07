package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.json.JConstant

class Codes {

    static final String PARAMS_TRACKLIST_MAX_DATA_COLUMNS = "hds.explorer.trackinglists.max_data_columns"
    static final String PARAMS_GENDER_CHECKING = "hds.explorer.constraints.gender.checking"
    static final String PARAMS_MIN_AGE_OF_FATHER = "hds.explorer.constraints.father.age.min"
    static final String PARAMS_MIN_AGE_OF_MOTHER = "hds.explorer.constraints.mother.age.min"
    static final String PARAMS_MIN_AGE_OF_HEAD   = "hds.explorer.constraints.head.age.min"
    static final String PARAMS_MIN_AGE_OF_SPOUSE   = "hds.explorer.constraints.spouse.age.min"
    static final String PARAMS_SYSTEM_LANGUAGE = "hds.explorer.system.language"
    static final String PARAMS_SYSTEM_CODE_GENERATOR = "hds.explorer.system.codegenerator"

    static final String MEMBER_UNKNOWN_CODE = "UNK"

    /* Mutable Constants - Will be loaded during bootstrap */
    static int MAX_TRACKLIST_DATA_COLUMNS_VALUE = 20
    static int MIN_MOTHER_AGE_VALUE = 12
    static int MIN_FATHER_AGE_VALUE = 12
    static int MIN_HEAD_AGE_VALUE = 12
    static int MIN_SPOUSE_AGE_VALUE = 16
    static boolean GENDER_CHECKING = true
    static String SYSTEM_LANGUAGE = "en"
    static String SYSTEM_CODE_GENERATOR = "org.philimone.hds.explorer.server.settings.generator.DefaultCodeGenerator"
    static List<JConstant> SYSTEM_ALL_CODE_GENERATORS = new ArrayList<>()
}
