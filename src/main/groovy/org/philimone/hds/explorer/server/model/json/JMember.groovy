package org.philimone.hds.explorer.server.model.json

import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.main.Member

import java.time.LocalDate

class JMember {
    String code
    String name
    String gender
    String dob
    String householdCode
    String householdName

    JMember() {
    }

    JMember(String code, String name, String gender, String dob, String householdCode, String householdName) {
        this.code = code
        this.name = name
        this.gender = gender
        this.dob = dob
        this.householdCode = householdCode
        this.householdName = householdName
    }

    JMember(String code, String name, Gender gender, LocalDate dob, String householdCode, String householdName) {
        this.code = code
        this.name = name
        this.gender = gender.code
        this.dob = net.betainteractive.utilities.StringUtil.format(dob)
        this.householdCode = householdCode
        this.householdName = householdName
    }

    JMember(Member member) {
        this.code = member.code
        this.name = member.name
        this.gender = member.gender
        this.dob = net.betainteractive.utilities.StringUtil.format(member.dob)
        this.householdCode = member.householdCode
        this.householdName = member.householdName
    }
}
