package org.philimone.hds.explorer.server.model.json

import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.main.Member

import java.time.LocalDate

class JMember {
    String code
    String name
    String gender
    String dob

    JMember() {
    }

    JMember(String code, String name, String gender, String dob) {
        this.code = code
        this.name = name
        this.gender = gender
        this.dob = dob
    }

    JMember(String code, String name, Gender gender, LocalDate dob) {
        this.code = code
        this.name = name
        this.gender = gender.code
        this.dob = net.betainteractive.utilities.StringUtil.format(dob)
    }

    JMember(Member member) {
        this.code = member.code
        this.name = member.name
        this.gender = member.gender
        this.dob = net.betainteractive.utilities.StringUtil.format(member.dob)
    }
}
