package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult

import java.time.LocalDate

import static org.springframework.http.HttpStatus.*

class RoundController {

    RoundService roundService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)
        respond roundService.list(params), model:[roundCount: roundService.count()]
    }

    def show(String id) {
        respond roundService.get(id)
    }

    def create() {

        def roundInstance = new Round(params)
        roundInstance.roundNumber = roundService.nextRoundNumber()

        respond roundInstance
    }

    def save(Round round) {
        if (round == null) {
            notFound()
            return
        }

        //println "params="+(params.startDate instanceof Date)
        println "params="+(params.startDate)

        //correct the params
        params.startDate = StringUtil.toLocalDateFromDate(params.getDate('startDate'))
        params.endDate = StringUtil.toLocalDateFromDate(params.getDate('endDate'))


        round = new Round(params)

        def result = roundService.createRound(round)

        if (result.status == RawExecutionResult.Status.SUCCESS) {
            round = result.domainInstance
        } else {

            respond round, view:'create', model: [errorMessages: result.errorMessages]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'round.label', default: 'Round'), round.roundNumber])
                redirect round
            }
            '*' { respond round, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond roundService.get(id)
    }

    def update(Round round) {
        if (round == null) {
            notFound()
            return
        }

        //println "params: ${params}, ${round.description}"

        params.startDate = StringUtil.toLocalDateFromDate(params.getDate('startDate'))
        params.endDate = StringUtil.toLocalDateFromDate(params.getDate('endDate'))
        round.startDate = params.startDate
        round.endDate = params.endDate

        try {

            bindData(round, params)

            round.save(flush:true)

            if (round.hasErrors()){
                respond round.errors, view:'edit'
                return
            }

            println "errors: ${round.errors}"

        } catch (ValidationException e) {
            respond round.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'round.label', default: 'Round'), round.roundNumber])
                redirect round
            }
            '*'{ respond round, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        roundService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'round.label', default: 'Round'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'round.label', default: 'Round'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
