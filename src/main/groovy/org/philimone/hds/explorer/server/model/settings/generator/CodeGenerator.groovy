package org.philimone.hds.explorer.server.model.settings.generator

import org.philimone.hds.explorer.server.model.authentication.User

interface CodeGenerator {

    boolean isModuleCodeValid(String code)

    boolean isTrackingListCodeValid(String code)

    boolean isRegionCodeValid(String code)

    boolean isHouseholdCodeValid(String code)

    boolean isMemberCodeValid(String code)

    boolean isVisitCodeValid(String code)

    boolean isUserCodeValid(String code)

    boolean isPregnancyCodeValid(String code)

    String generateModuleCode(String moduleName, List<String> existentCodes)

    String generateTrackingListCode(List<String> existentCodes)

    String generateRegionCode(String regionName, List<String> existentCodes)

    String generateHouseholdCode(String baseCode, List<String> existentCodes)

    String generateMemberCode(String baseCode, List<String> existentCodes)

    String generateVisitCode(String baseCode, List<String> existentCodes)

    String generateUserCode(User user, List<String> existentCodes)

    String generatePregnancyCode(String baseCode, List<String> existentCodes)

}