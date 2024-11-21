package org.philimone.hds.explorer.server.settings.generator

import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Round

import javax.annotation.Nonnull

/*
 * Code Generator Template Interface
 * To add new selectable code generators you must implement this interface in this package only (org.philimone.hds.explorer.server.settings.generator)
 * After creating your implementation of HDS-Explorer Code generator the system will detect them automatically and you will be able to select it in the webapp
 */

interface CodeGenerator {

    @Nonnull String getName()

    boolean isModuleCodeValid(String code)

    boolean isTrackingListCodeValid(String code)

    boolean isRegionCodeValid(RegionLevel lowestRegionLevel, RegionLevel codeRegionLevel, String code)

    boolean isHouseholdCodeValid(String code)

    boolean isMemberCodeValid(String code)

    boolean isVisitCodeValid(String code)

    boolean isUserCodeValid(String code)

    boolean isPregnancyCodeValid(String code)

    String generateModuleCode(String moduleName, List<String> existentCodes)

    String generateTrackingListCode(List<String> existentCodes)

    String generateRegionCode(RegionLevel lowestRegionLevel, Region parentRegion, String regionName, List<String> existentCodes)

    String generateHouseholdCode(String baseCode, List<String> existentCodes)

    String generateMemberCode(String baseCode, List<String> existentCodes)

    String generateVisitCode(String baseCode, List<String> existentCodes)

    String generateUserCode(User user, List<String> existentCodes)

    String generatePregnancyCode(String baseCode, List<String> existentCodes)

    String getHouseholdBaseCode(Region region, User user)

    String getVisitBaseCode(Household household, Round round)

    String getModuleSampleCode()

    String getTrackingListSampleCode()

    String getRegionSampleCode(RegionLevel lowestRegionLevel, RegionLevel regionLevel)

    String getHouseholdSampleCode()

    String getMemberSampleCode()

    String getVisitSampleCode()

    String getUserSampleCode()

    String getPregnancySampleCode()

}