package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.settings.Codes

import static org.springframework.http.HttpStatus.*

class MemberController {

    MemberService memberService
    HouseholdService householdService
    def generalUtilitiesService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)
        render view: "index"
    }

    def show(String id) {
        respond memberService.get(id)
    }

    def showbycode(String id) {
        println(id)
        redirect action: 'show', id: memberService.getMember(id)?.id
    }

    def create() {
        respond new Member(params)
    }

    def save(Member member) {
        if (member == null) {
            notFound()
            return
        }

        try {
            memberService.save(member)
        } catch (ValidationException e) {
            respond member.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'member.label', default: 'Member'), member.id])
                redirect member
            }
            '*' { respond member, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond memberService.get(id)
    }

    def update(Member member) {
        if (member == null) {
            notFound()
            return
        }

        try {
            memberService.save(member)
        } catch (ValidationException e) {
            respond member.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'member.label', default: 'Member'), member.id])
                redirect member
            }
            '*'{ respond member, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        memberService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'member.label', default: 'Member'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'member.label', default: 'Member'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def showHousehold = {
        def household = householdService.getHousehold(params.id)

        redirect controller: "household", action: "show", id: household.id
    }

    def memberList = {
        def dateUtil = generalUtilitiesService.getDateUtil()

        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }

        //code, name, gender, dob, householdCode, collectedDate, createdDate

        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            ne('code', Codes.MEMBER_UNKNOWN_CODE)
            or {
                if (search_filter) ilike 'code', search_filter
                if (search_filter) ilike 'name', search_filter
                if (search_filter) ilike 'gender', search_filter
                if (search_filter) ilike 'householdCode', search_filter
            }
        }

        //ORDERS
        def orderer = Member.withCriteria {
            filterer.delegate = delegate
            filterer()
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'code':          order 'code',          oi[1]; break
                    case 'name':          order 'name',          oi[1]; break
                    case 'gender':        order 'gender',        oi[1]; break
                    case 'dob':           order 'dob',           oi[1]; break
                    case 'householdCode': order 'householdCode', oi[1]; break
                    case 'collectedDate': order 'collectedDate', oi[1]; break
                    case 'createdDate':   order 'createdDate',   oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }


        //Display records
        def members = orderer.collect { member ->
            ['code':     "<a href='${createLink(controller: 'member', action: 'show', id: member.id)}'>${member.code}</a>",
             'name':     member.name,
             'gender':   message(code: member.gender.name),
             'dob':      dateUtil.formatYMD(member.dob), //StringUtil.formatLocalDate(member.dob),
             'householdCode':"<a href='${createLink(controller: 'household', action: 'show', id: member.household?.id)}'>${member.householdCode}</a>",
             'collectedDate': dateUtil.formatYMDHMS(member.collectedDate), // StringUtil.formatLocalDateTime(member.collectedDate),
             'createdDate': dateUtil.formatYMDHMS(member.createdDate) //StringUtil.formatLocalDateTime(member.createdDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = Member.count()
        def memberCriteria = Member.createCriteria()
        def recordsFiltered = memberCriteria.count {
            filterer.delegate = delegate
            filterer()
        }


        //println "member recordsTotal $recordsTotal"
        //println "member recordsFiltered $recordsFiltered"
        //println "members ${members.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: members]

        render result as JSON
    }

    def memberCodesList = {
        //println("params ${params.term}")

        def result = Member.executeQuery("select m.code, m.name from Member m where m.code like ?0 or m.name like ?0 order by m.code", ["%"+params.term+"%"], [max: 40])
        def codes = result.collect {
            ['value': it[0], 'label': "${it[0]} - ${it[1]}"]
        }

        render codes as JSON
    }
}
