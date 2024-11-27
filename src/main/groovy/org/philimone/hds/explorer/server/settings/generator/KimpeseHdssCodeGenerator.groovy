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

class KimpeseHdssCodeGenerator implements CodeGenerator {

    final String MODULE_CODE_PATTERN = '^MX-[0-9]{3}$'
    final String TRACKLIST_CODE_PATTERN = '^TR-[0-9]{6}$'
    final String REGION_LEVEL_1_PATTERN = '^[A-Z0-9]{3}$' //Zone de Sante
    final String REGION_LEVEL_2_PATTERN = '^[A-Z0-9]{3}$' //Secteur
    final String REGION_LEVEL_3_PATTERN = '^[A-HJ-NP-Z]$' //Aire de Sante
    final String REGION_LEVEL_4_PATTERN = '^[A-HJ-NP-Z][0-9]{2}$' //Village
    final String REGION_LEVEL_5_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z]$' //Zone de dénombrement (ZD)
    final String REGION_LEVEL_6_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}$' //Concession

    final String HOUSEHOLD_CODE_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}$'
    final String MEMBER_CODE_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}[0-9]{2}$'
    final String VISIT_CODE_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}-[0-9]{3}-[0-9]{3}$' //HOUSEHOLD+ROUND+ORDINAL
    final String USER_CODE_PATTERN = '^[A-Z0-9]{3}$'
    final String PREGNANCY_CODE_PATTERN = '^[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}[0-9]{2}-[0-9]{2}$'

    final String CHARS_A_TO_Z = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    final String CHARS_A_TO_Z_EXCEPT_IO = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    final String CHARS_0_TO_9 = "0123456789";

    @Override
    String getName() {
        return "Kimpese HDSS Code Scheme Generator (DRC)"
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

        switch (codeRegionLevel) {
            case RegionLevel.HIERARCHY_1: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_1_PATTERN)
            case RegionLevel.HIERARCHY_2: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_2_PATTERN)
            case RegionLevel.HIERARCHY_3: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_3_PATTERN)
            case RegionLevel.HIERARCHY_4: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_4_PATTERN)
            case RegionLevel.HIERARCHY_5: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_5_PATTERN)
            case RegionLevel.HIERARCHY_6: return !StringUtil.isBlank(code) && code.matches(REGION_LEVEL_6_PATTERN)
        }

        return false
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

        //check levels

        switch (regionLevel) {
            case RegionLevel.HIERARCHY_1: return generateRegularRegionCode(regionName, existentCodes)
            case RegionLevel.HIERARCHY_2: return generateRegularRegionCode(regionName, existentCodes)
            case RegionLevel.HIERARCHY_3: return generateRegionLevel3(existentCodes)
            case RegionLevel.HIERARCHY_4: return generateRegionLevel4(parentRegion, existentCodes)
            case RegionLevel.HIERARCHY_5: return generateRegionLevel5(parentRegion, existentCodes)
            case RegionLevel.HIERARCHY_6: return generateRegionLevel6(parentRegion, existentCodes)
        }


        return null
    }

    String generateRegularRegionCode(String regionName, List<String> existentCodes) {
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

    String generateRegionLevel3(List<String> existentCodes) {
        //Aire de Sante - [A-HJ-NP-Z]
        def chars = CHARS_A_TO_Z_EXCEPT_IO.toCharArray()

        for (def c : chars){
            if (c==' ') continue

            def test = "${c}" as String

            if (!existentCodes.contains(test)){
                return test
            }
        }

        return null
    }

    String generateRegionLevel4(Region parentRegion, List<String> existentCodes) {
        //Village - [A-HJ-NP-Z][0-9]{2} = Aire de Sante + 00

        String baseCode = parentRegion.code

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}01"
        } else {
            List<String> listCodes = new ArrayList<>();
            for (String code : existentCodes) {
                if (code.startsWith(baseCode)) {
                    listCodes.add(code);
                }
            }

            int number = 1;
            int max = 99
            if (CodeGeneratorFactory.INCREMENTAL_RULE == CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE) {
                try {
                    String lastCode = listCodes.get(listCodes.size() - 1);
                    String lastCodeNumber = lastCode.replaceFirst(baseCode, "");
                    number = StringUtil.isBlank(lastCodeNumber) ? 1 : (Integer.parseInt(lastCodeNumber) + 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (int i=number; i <= max; i++){
                def code = "${baseCode}${String.format('%02d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    String generateRegionLevel5(Region parentRegion, List<String> existentCodes) {
        //Zone de dénombrement (ZD) - [A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z] = Village + A

        String baseCode = parentRegion.code

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}A"
        } else {
            List<String> listCodes = new ArrayList<>();
            for (String code : existentCodes) {
                if (code.startsWith(baseCode)) {
                    listCodes.add(code);
                }
            }

            char[] chars = CHARS_A_TO_Z_EXCEPT_IO.toCharArray()
            int index = 0;
            int max = chars.length;

            if (CodeGeneratorFactory.INCREMENTAL_RULE == CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE) {
                try {
                    String lastCode = listCodes.get(listCodes.size() - 1);
                    String lastCodeLetter = lastCode.replaceFirst(baseCode, "");
                    index = StringUtil.isBlank(lastCodeLetter) ? 0 : (CHARS_A_TO_Z_EXCEPT_IO.indexOf(lastCodeLetter) + 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (int i=index; i < max; i++){
                def code = "${baseCode}${chars[i]}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    String generateRegionLevel6(Region parentRegion, List<String> existentCodes) {
        //Concession - [A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3} = ZD + 000

        String baseCode = parentRegion.code

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}001"
        } else {
            List<String> listCodes = new ArrayList<>();
            for (String code : existentCodes) {
                if (code.startsWith(baseCode)) {
                    listCodes.add(code);
                }
            }

            int number = 1;
            int max = 999
            if (CodeGeneratorFactory.INCREMENTAL_RULE == CodeGeneratorIncrementalRule.INCREMENT_LAST_CODE) {
                try {
                    String lastCode = listCodes.get(listCodes.size() - 1);
                    String lastCodeNumber = lastCode.replaceFirst(baseCode, "");
                    number = StringUtil.isBlank(lastCodeNumber) ? 1 : (Integer.parseInt(lastCodeNumber) + 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (int i=number; i <= max; i++){
                def code = "${baseCode}${String.format('%03d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }


    @Override
    String generateHouseholdCode(String baseCode, List<String> existentCodes) {
        //[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}
        //Generate codes and try to match the database until u cant

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}01" //just add 3 more characters to the Menage code
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
            }

            for (int i=number; i <= 99; i++){
                def code = "${baseCode}${String.format('%02d', i)}" as String
                if (!existentCodes.contains(code)){
                    return code
                }
            }
        }

        return "${baseCode}ERROR"
    }

    @Override
    String generateMemberCode(String baseCode, List<String> existentCodes) {
        //[A-HJ-NP-Z][0-9]{2}[A-HJ-NP-Z][0-9]{3}[0-9]{2}[0-9]{2}
        //Generate codes and try to match the database until u cant

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}01" //just add 2 more characters
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
            }

            for (int i=number; i <= 99; i++){
                def code = "${baseCode}${String.format('%02d', i)}" as String
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

        switch (regionLevel) {
            case RegionLevel.HIERARCHY_1: return "CAT";
            case RegionLevel.HIERARCHY_2: return "TXU";
            case RegionLevel.HIERARCHY_3: return "A";
            case RegionLevel.HIERARCHY_4: return "A01";
            case RegionLevel.HIERARCHY_5: return "A01C";
            case RegionLevel.HIERARCHY_6: return "A01C001";
        }

        return "TXU"
    }

    @Override
    String getHouseholdSampleCode() {
        return "A01C00103"
    }

    @Override
    String getMemberSampleCode() {
        return "A01C0010301"
    }

    @Override
    String getVisitSampleCode() {
        return "A01C00103-000-001"
    }

    @Override
    String getUserSampleCode() {
        return "PF1"
    }

    @Override
    String getPregnancySampleCode() {
        return "A01C0010301-01"
    }
}
