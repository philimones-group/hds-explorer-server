package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorIncrementalRule

class Codes {

    static final String PARAMS_GENDER_CHECKING = "hds.explorer.constraints.gender.checking"
    static final String PARAMS_MIN_AGE_OF_FATHER = "hds.explorer.constraints.father.age.min"
    static final String PARAMS_MIN_AGE_OF_MOTHER = "hds.explorer.constraints.mother.age.min"
    static final String PARAMS_MIN_AGE_OF_HEAD   = "hds.explorer.constraints.head.age.min"
    static final String PARAMS_MIN_AGE_OF_SPOUSE   = "hds.explorer.constraints.spouse.age.min"
    static final String PARAMS_MIN_AGE_OF_RESPONDENT   = "hds.explorer.constraints.respondent.age.min"
    static final String PARAMS_MAX_ANTEPARTUM_VISITS = "hds.explorer.constraints.pregnancy.antepartum.visits.max"
    static final String PARAMS_MAX_POSTPARTUM_VISITS = "hds.explorer.constraints.pregnancy.postpartum.visits.max"
    static final String PARAMS_SYSTEM_LANGUAGE = "hds.explorer.system.language"
    static final String PARAMS_SYSTEM_CODE_GENERATOR = "hds.explorer.system.codegenerator"
    static final String PARAMS_SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE = "hds.explorer.system.codegenerator_rules.incremental"
    static final String PARAMS_SYSTEM_REGION_HEAD_SUPPORT = "hds.explorer.system.region.head.support"
    static final String PARAMS_SYSTEM_VISIT_GPS_REQUIRED = "hds.explorer.system.visit.gps.required"
    public static final String PARAMS_SYSTEM_USE_ETHIOPIAN_CALENDAR = "hds.explorer.system.use.ethiopian.calendar";
    static final String PARAMS_SYSTEM_HOMEPATH = "hds.explorer.system.path"

    static final String MEMBER_UNKNOWN_CODE = "UNK"

    /* Mutable Constants - Will be loaded during bootstrap */
    static int MIN_MOTHER_AGE_VALUE = 12
    static int MIN_FATHER_AGE_VALUE = 12
    static int MIN_HEAD_AGE_VALUE = 12
    static int MIN_SPOUSE_AGE_VALUE = 16
    static int MIN_RESPONDENT_AGE_VALUE = 12
    static int MAX_ANTEPARTUM_VISITS = 4
    static int MAX_POSTPARTUM_VISITS = 4
    static boolean GENDER_CHECKING = true
    static String SYSTEM_LANGUAGE = "en"
    static String SYSTEM_CODE_GENERATOR = "org.philimone.hds.explorer.server.settings.generator.DefaultSimpleCodeGenerator"
    static boolean SYSTEM_REGION_HEAD_SUPPORT = false
    static boolean SYSTEM_VISIT_GPS_REQUIRED = false
    static boolean SYSTEM_USE_ETHIOPIAN_CALENDAR = false
    static String SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE = CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE.code

    static String SYSTEM_HOMEPATH = "/var/lib/hds-explorer"
    static List<JConstant> SYSTEM_ALL_CODE_GENERATORS = new ArrayList<>()

    final static String SYSTEM_SUPPORTED_CALENDAR_GREGORIAN = "gregorian"
    final static String SYSTEM_SUPPORTED_CALENDAR_ETHIOPIAN = "ethiopian"

}
