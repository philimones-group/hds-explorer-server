package org.philimone.hds.explorer.server.model.settings.generator

import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.main.Household

/*
 * The HDS-Explorer Default code generator (different sites can implement they own type of codes)
 */
class DefaultCodeGenerator implements CodeGenerator {

    final String REGION_CODE_PATTERN = '^[A-Z0-9]{3}$'
    final String HOUSEHOLD_CODE_PATTERN = '^[A-Z0-9]{6}[0-9]{3}$'
    final String MEMBER_CODE_PATTERN = '^[A-Z0-9]{6}[0-9]{6}$'
    final String VISIT_CODE_PATTERN = '^[A-Z0-9]{6}[0-9]{6}$'
    final String USER_CODE_PATTERN = '^[A-Z0-9]{3}$'

    @Override
    boolean isRegionCodeValid(String code) {
        return !StringUtil.isBlank(code) && code.matches(REGION_CODE_PATTERN)
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
    String generateRegionCode(String regionName, List<String> existentCodes) {

        if (StringUtil.isBlank(regionName)) return null

        //first 3 characters
        def u = regionName.toUpperCase()

        def chars = ("1".."9") + ("A".."Z")
        def alist = [u.chars[0]]
        def blist = (u.length()>1) ? (u.substring(1).chars.toList()) : chars
        def clist = (u.length()>2) ? (u.substring(2).chars.toList() + chars) : chars

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

        return null
    }

    @Override
    String generateHouseholdCode(String baseCode, List<String> existentCodes) {
        //Generate codes and try to match the database until u cant

        if (StringUtil.isBlank(baseCode)) return null

        if (existentCodes.size()==0){
            return "${baseCode}001"
        } else {
            for (int i=1; i <= 999; i++){
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
            return "${baseCode}001"
        } else {
            for (int i=1; i <= 999; i++){
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
            return "${baseCode}001"
        } else {

            def first = existentCodes.last()
            def sorder = first.replaceAll(baseCode, "")
            def n = StringUtil.getInteger(sorder)

            if (n==null) n = 1
            for (int i=n; i <= 999; i++){
                def code = "${baseCode}${String.format('%03d', i)}" as String
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

        if (user.username.matches(regexFw)){ //ohds fieldworker
            return user.username.toUpperCase().replaceAll("FW")
        }else {
            //def codes = User.list().collect{ t -> t.code}

            def f = user.firstName.toUpperCase()
            def l = user.lastName.toUpperCase()
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
}
