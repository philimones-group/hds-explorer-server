<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawPregnancyOutcome.label', default: 'RawPregnancyOutcome')}" />
    <title><g:message code="rawPregnancyOutcome.${mode}.label" args="[entityName]" /></title>

    <style>

        .rerrors {
            font-size: 1em;
            line-height: 2;
            margin: 1em 2em;
            padding: 0.25em;
        }

        .rerrors {
            background: #d6e7d0;
            border: 2px solid #ffaaaa;
            color: #cc0000;
            -moz-box-shadow: 0 0 0.25em #ff8888;
            -webkit-box-shadow: 0 0 0.25em #ff8888;
            box-shadow: 0 0 0.25em #ff8888;
        }

        .rerrors_div {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            margin: 0.8em 1.3em 0.3em;
        }

        .rerrors_title {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            padding: 0 0.3em;
        }

        .rerrors ul {
            padding: 1em;
        }

        .errors li {
            list-style: none;
            background: transparent url(../images/skin/exclamation.png) 0.5em 50% no-repeat;
            text-indent: 2.2em;
            padding: 0 0.25em;
        }

    </style>

    <dt:defaultResources />
    <tb:tabulatorResources />
    <tb:luxonResources/>
    <tb:jquiResources/>
    <bi:kwCalendarResources/>
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawPregnancyOutcome" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawPregnancyOutcome" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawPregnancyOutcome.${mode}.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        <br>
    </g:if>


    <g:if test="${errorMessages}">
        <div class="rerrors_div">
            <p class="rerrors_title"><b><g:message code="rawDomain.errors.messages.title.label" /></b></p>
            <ul class="rerrors" role="alert">
                <g:each in="${errorMessages}" status="i" var="error">
                    <li data-field-id="${error.text}">${error.text}</li>
                </g:each>
            </ul>
        </div>
        <br>
    </g:if>

    <g:hasErrors bean="${this.rawPregnancyOutcome}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawPregnancyOutcome}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawPregnancyOutcome?.id}" />
        <g:hiddenField name="version" value="${this.rawPregnancyOutcome?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawPregnancyOutcome}" property="id"    label="rawPregnancyOutcome.id.label" mode="show" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="code"    label="rawPregnancyOutcome.code.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="motherCode"    label="rawPregnancyOutcome.motherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="fatherCode"    label="rawPregnancyOutcome.fatherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="numberOfOutcomes"    label="rawPregnancyOutcome.numberOfOutcomes.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyOutcome}" property="outcomeDate"    label="rawPregnancyOutcome.outcomeDate.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="birthPlace"    label="rawPregnancyOutcome.birthPlace.label" mode="${mode}" options="BirthPlace" nullable="true" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="birthPlaceOther"    label="rawPregnancyOutcome.birthPlaceOther.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="visitCode"    label="rawPregnancyOutcome.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="modules"    label="rawPregnancyOutcome.modules.label" mode="${mode}" />

            <g:if test="${this.rawPregnancyOutcome?.childs}">

                <div class="fieldcontain">
                    <span id="relationships-label" class="property-label">
                        <g:message code="rawPregnancyOutcome.childs.label" />
                    </span>
                    <span class="property-value" aria-labelledby="childs-label">
                        <ul>
                            <g:each in="${this.rawPregnancyOutcome.childs}" var="child">
                                <li class="list-style-type: square;">
                                    <g:link controller="rawDomain" action="editPregnancyChild" id="${child.id}">
                                        ${child?.toString()}
                                    </g:link>
                                </li>
                            </g:each>
                        </ul>
                    </span>
                </div>

            </g:if>

            <bi:field bean="${this.rawPregnancyOutcome}" property="collectedBy"    label="rawPregnancyOutcome.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="collectedDate"    label="rawPregnancyOutcome.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="uploadedDate"    label="rawPregnancyOutcome.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawPregnancyOutcome}" property="processedStatus"    label="rawPregnancyOutcome.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawPregnancyOutcome.visitCode?.replaceAll('-.+','')}" />
        <g:set var="household_name" value="${member?.householdName}" />
        <g:set var="member_code" value="${this.rawPregnancyOutcome.motherCode}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updatePregnancyOutcome" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updatePregnancyOutcome" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidatePregnancyOutcome" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deletePregnancyOutcome" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_pregnancies">
                    <g:message code="rawDomain.helpers.button.show.member.pregnancies.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:if test="${rawPregnancyRegId}" >
                    <g:hiddenField name="dependencyEventId" value="${rawPregnancyRegId}" />
                    <g:hiddenField name="dependencyEventEntity" value="${org.philimone.hds.explorer.server.model.enums.RawEntity.PREGNANCY_REGISTRATION.name()}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.raw.pregnancyreg.label" />
                    </button>
                </g:if>

                <g:render template="show_member_residencies_hrelationships"/>

                <g:render template="show_household_residents"/>

                <g:render template="show_member_pregnancies"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editPregnancyOutcome" id="${this.rawPregnancyOutcome.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
