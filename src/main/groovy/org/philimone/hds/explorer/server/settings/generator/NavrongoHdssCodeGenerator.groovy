package org.philimone.hds.explorer.server.settings.generator

import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Round

/*
 * The HDS-Explorer Simple Code generator (creates Household Codes without User/Fieldworker Code)
 */

class NavrongoHdssCodeGenerator implements CodeGenerator {

    final String MODULE_CODE_PATTERN = '^MX-[0-9]{3}$'
    final String TRACKLIST_CODE_PATTERN = '^TR-[0-9]{6}$'
    final String REGION_CODE_PATTERN = '^[A-Z0-9]{3}$'
    final String COMPOUND_CODE_PATTERN = '^[A-Z0-9]{3}[0-9]{6}$'
    final String HOUSEHOLD_CODE_PATTERN = '^[A-Z0-9]{3}[0-9]{9}$'
    final String MEMBER_CODE_PATTERN = '^[A-Z0-9]{3}[0-9]{12}$'
    final String VISIT_CODE_PATTERN = '^[A-Z0-9]{3}[0-9]{9}-[0-9]{3}-[0-9]{3}$' //TXU000001-000-001 - HOUSEHOLD+ROUND+ORDINAL
    final String USER_CODE_PATTERN = '^[A-Z0-9]{3}$'
    final String PREGNANCY_CODE_PATTERN = '^[A-Z0-9]{3}[0-9]{12}-[0-9]{2}$'

    @Override
    String getName() {
        return "Navrongo HDSS Compound-Based Code Scheme Generator"
    }

    @Override
    boolean isModuleCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(MODULE_CODE_PATTERN)
    }

    @Override
    boolean isTrackingListCodeValid(String code) {
        return false
    }

    @Override
    boolean isRegionCodeValid(RegionLevel lowestRegionLevel, RegionLevel codeRegionLevel, String code) {
        //On Compound based code generator the lowestRegionLevel is a Compound with code that differs from other region levels

        if (lowestRegionLevel == codeRegionLevel) {
            return !StringUtil.isBlank(code) && code.matches(COMPOUND_CODE_PATTERN)
        } else {
            return !StringUtil.isBlank(code) && code.matches(REGION_CODE_PATTERN)
        }
    }

    @Override
    boolean isHouseholdCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(HOUSEHOLD_CODE_PATTERN)
    }

    @Override
    boolean isMemberCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(MEMBER_CODE_PATTERN)
    }

    @Override
    boolean isVisitCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(VISIT_CODE_PATTERN)
    }

    @Override
    boolean isUserCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(USER_CODE_PATTERN)
    }

    @Override
    boolean isPregnancyCodeValid(String code){
        return !StringUtil.isBlank(code) && code.matches(PREGNANCY_CODE_PATTERN)
    }

    @Override
    String generateModuleCode(String moduleName, List<String> existentCodes) {
        //MX-001,MX-002,MX-099
        def base = "MX-"
        println "exists ${existentCodes}"
        if (existentCodes == null) return "${base}001"

        for (int i = 1; i <= 99 ; i++) {
            def test = "${base}${String.format("%03d", i)}" as String
            if (!existentCodes.contains(test)) {
                return test
            }
        }

        return null

    }

    @Override
    String generateTrackingListCode(List<String> existentCodes) {
        def baseCode = "TR-"
        if (existentCodes.size()==0){
            return "${baseCode}000001"
        } else {
            for (int i=1; i <= 999999; i++){
                def code = "${baseCode}${String.format('%06d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    @Override
    String generateRegionCode(RegionLevel lowestRegionLevel, Region parentRegion, String regionName, List<String> existentCodes) {

        if (StringUtil.isBlank(regionName)) return null

        def regionLevel = (parentRegion==null) ? RegionLevel.HIERARCHY_1 : parentRegion.hierarchyLevel.nextLevel()

        if (lowestRegionLevel == regionLevel) {
            return generateCompoundRegionCode(parentRegion, regionName, existentCodes)
        }

        return generateRegularRegionCode(parentRegion, regionName, existentCodes)
    }

    String generateRegularRegionCode(Region parentRegion, String regionName, List<String> existentCodes) {

        if (StringUtil.isBlank(regionName)) return null

        //first 3 characters
        def u = StringUtil.removeAcentuation(regionName.trim().toUpperCase())
        u = StringUtil.removeNonAlphanumeric(u)

        def chars = ("1".."9") + ("A".."Z")
        def alist = [u.chars[0]]
        def blist = (u.length()>1) ? (u.substring(1).chars.toList()) : chars
        def clist = (u.length()>2) ? (u.substring(2).chars.toList() + chars) : chars

        for (def a : alist){
            if (a==' ') continue
            for (def b : blist){
                if (b==' ') continue
                for (def c : clist){
                    if (c==' ') continue

                    def test = "${a}${b}${c}" as String

                    if (!existentCodes.contains(test)){
                        return test
                    }
                }

            }
        }

        return null
    }

    String generateCompoundRegionCode(Region parentRegion, String regionName, List<String> existentCodes) {
        //Other implementations of codegenerators can use a different approach to generate lowest region level codes

        //This is a Compound Code - similar to openhds location
        String baseCode = parentRegion.code

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}000001"
        } else {
            List<String> listCodes = new ArrayList<>();
            for (String code : existentCodes) {
                if (code.startsWith(baseCode)) {
                    listCodes.add(code);
                }
            }

            int number = 1;
            if (CodeGeneratorFactory.INCREMENTAL_RULE == CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE) {
                try {
                    String lastCode = listCodes.get(listCodes.size() - 1);
                    String lastCodeNumber = lastCode.replaceFirst(baseCode, "");
                    number = Integer.parseInt(lastCodeNumber) + 1;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (int i=number; i <= 999999; i++){
                def code = "${baseCode}${String.format('%06d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    @Override
    String generateHouseholdCode(String baseCode, List<String> existentCodes) {
        //Generate codes and try to match the database until u cant

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}001" //just add 3 more characters to the compound code
        } else {
            int number = 1;
            if (CodeGeneratorFactory.INCREMENTAL_RULE == CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE) {
                try {
                    String lastCode = existentCodes.get(existentCodes.size() - 1);
                    String lastCodeNumber = lastCode.replaceFirst(baseCode, "");
                    number = Integer.parseInt(lastCodeNumber) + 1;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                number = number > 999 ? 1 : number //will try to fill the gaps
            }

            for (int i=number; i <= 999; i++){
                def code = "${baseCode}${String.format('%03d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    @Override
    String generateMemberCode(String baseCode, List<String> existentCodes) {
        //Generate codes and try to match the database until u cant

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}001" //just add 2 more characters
        } else {
            int number = 1;
            //This approach will be filling the gaps
            for (int i=number; i <= 999; i++){
                def code = "${baseCode}${String.format('%03d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    @Override
    String generateVisitCode(String baseCode, List<String> existentCodes) {

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}-001"
        } else {

            def first = existentCodes.last()
            def sorder = first.replaceAll(baseCode+"-", "")
            def n = StringUtil.getInteger(sorder)

            if (n==null) n = 1
            for (int i=n; i <= 999; i++){
                def code = "${baseCode}-${String.format('%03d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return null
    }

    @Override
    String generateUserCode(User user, List<String> existentCodes) {
        def regexFw = '^FW[A-Za-z]{3}$'

        if (user.username?.matches(regexFw)){ //ohds fieldworker
            return user.username.toUpperCase().replaceAll("FW", "")
        }else {
            //def codes = User.list().collect{ t -> t.code}

            def f = StringUtil.removeAcentuation(user.firstName.toUpperCase())
            def l = StringUtil.removeAcentuation(user.lastName.toUpperCase())

            f = StringUtil.removeNonAlphanumeric(f)
            l = StringUtil.removeNonAlphanumeric(l)

            def alist = f.chars.toList()
            def blist = l.chars.toList()
            def clist = ("1".."9") + (l.length()>1 ? l.substring(1).chars.toList() : []) + ("A".."Z")

            for (def a : alist){
                for (def b : blist){
                    for (def c : clist){
                        def test = "${a}${b}${c}" as String

                        if (!existentCodes.contains(test)){
                            return test
                        }
                    }

                }
            }
        }

        return null
    }

    @Override
    String generatePregnancyCode(String baseCode, List<String> existentCodes) {
        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}-01"
        } else {

            def first = existentCodes.last()
            def sorder = first.replaceAll("${baseCode}-", "")
            def n = StringUtil.getInteger(sorder)

            if (n==null) n = 1
            for (int i=n; i <= 99; i++){
                def code = "${baseCode}-${String.format('%02d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}-FULL"

    }

    @Override
    String getHouseholdBaseCode(Region region, User user) {
        return "${region.code}"
    }

    @Override
    String getVisitBaseCode(Household household, Round round) {
        long roundNumber = round.roundNumber
        return "${household.code}-" + String.format('%03d', roundNumber)
    }

    @Override
    String getModuleSampleCode() {
        return "MX-001"
    }

    @Override
    String getTrackingListSampleCode() {
        return "TR-000001"
    }

    @Override
    String getRegionSampleCode(RegionLevel lowestRegionLevel, RegionLevel regionLevel) {
        if (lowestRegionLevel == regionLevel) {
            return "TXU000001"
        } else {
            return "TXU"
        }
    }

    @Override
    String getHouseholdSampleCode() {
        return "TXU000001001"
    }

    @Override
    String getMemberSampleCode() {
        return "TXU000001001001"
    }

    @Override
    String getVisitSampleCode() {
        return "TXU000001001-000-001"
    }

    @Override
    String getUserSampleCode() {
        return "PF1"
    }

    @Override
    String getPregnancySampleCode() {
        return "TXU000001001001-01"
    }
}
