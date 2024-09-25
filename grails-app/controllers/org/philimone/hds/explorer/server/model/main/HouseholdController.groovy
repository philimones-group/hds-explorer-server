package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.logs.LogReportFile

class HouseholdController {

    HouseholdService householdService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)
        render view: "index"
    }

    def show(String id) {
        def household = householdService.get(id)
        def residentsList = householdService.getResidentMembers(household)
        respond household, model: [residentsList: residentsList]

        println "class name: " + household.class.name
    }

    def create() {
        respond new Household(params)
    }

    def save(Household household) {
        if (household == null) {
            notFound()
            return
        }

        try {
            householdService.save(household)
        } catch (ValidationException e) {
            respond household.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'household.label', default: 'Household'), household.id])
                redirect household
            }
            '*' { respond household, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond householdService.get(id)
    }

    def update(Household household) {
        if (household == null) {
            notFound()
            return
        }

        try {
            householdService.save(household)
        } catch (ValidationException e) {
            respond household.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'household.label', default: 'Household'), household.id])
                redirect household
            }
            '*'{ respond household, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        householdService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'household.label', default: 'Household'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'household.label', default: 'Household'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def householdList = {

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
        def logReportFileInstance = LogReportFile.get(params.logReportFileId)

        //code, name, headCode, headName, collectedDate, createdDate

        //println()
        //println "household orderList $orderList"


        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            or {
                if (search_filter) ilike 'code', search_filter
                if (search_filter) ilike 'name', search_filter
                if (search_filter) ilike 'headCode', search_filter
                if (search_filter) ilike 'headName', search_filter
            }
        }

        //ORDERS
        def orderer = Household.withCriteria {
            filterer.delegate = delegate
            filterer()
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'code':          order 'code',          oi[1]; break
                    case 'name':          order 'name',          oi[1]; break
                    case 'headCode':      order 'headCode',      oi[1]; break
                    case 'headName':      order 'headName',      oi[1]; break
                    case 'collectedDate': order 'collectedDate', oi[1]; break
                    case 'createdDate':   order 'createdDate',   oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }


        //Display records
        def households = orderer.collect { household ->
            ['code':     "<a href='${createLink(controller: 'household', action: 'show', id: household.id)}'>${household.code}</a>",
             'name':     household.name,
             'headCode': "<a href='${createLink(controller: 'member', action: 'show', id: household?.headMember?.id)}'>${household.headCode==null ? '' : household.headCode}</a>",
             'headName': household.headName,
             'collectedDate': StringUtil.format(household.collectedDate),
             'createdDate': StringUtil.format(household.createdDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = Household.count()
        def householdCriteria = Household.createCriteria()
        def recordsFiltered = householdCriteria.count {
            filterer.delegate = delegate
            filterer()
        }


        //println "household recordsTotal $recordsTotal"
        //println "household recordsFiltered $recordsFiltered"
        //println "households ${households.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: households]

        render result as JSON
    }

    def householdCodesList = {
        //println("params ${params.term}")

        def result = Household.executeQuery("select h.code, h.name from Household h where h.code like ?0 or h.name like ?0 order by h.code", ["%"+params.term+"%"], [max: 40])
        def codes = result.collect {
            ['value': it[0], 'label': "${it[0]} - ${it[1]}"]
        }

        render codes as JSON
    }
}
