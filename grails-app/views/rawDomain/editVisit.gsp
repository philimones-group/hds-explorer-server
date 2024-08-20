<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawVisit.label', default: 'RawVisit')}" />
    <title><g:message code="rawVisit.${mode}.label" args="[entityName]" /></title>

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
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawVisit" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawVisit" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawVisit.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawVisit}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawVisit}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawVisit?.id}" />
        <g:hiddenField name="version" value="${this.rawVisit?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawVisit}" property="id"    label="rawVisit.id.label" mode="show" />
            <bi:field bean="${this.rawVisit}" property="code"    label="rawVisit.code.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="householdCode"    label="rawVisit.householdCode.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="roundNumber"    label="rawVisit.roundNumber.label" mode="${mode}" />
            <bi:dateField bean="${this.rawVisit}" property="visitDate"    label="rawVisit.visitDate.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="visitLocation"    label="rawVisit.visitLocation.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="visitLocationOther"    label="rawVisit.visitLocationOther.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="visitReason"    label="rawVisit.visitReason.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="respondentResident"    label="rawVisit.respondentResident.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="respondentRelationship"    label="rawVisit.respondentRelationship.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="respondentCode"    label="rawVisit.respondentCode.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="respondentName"    label="rawVisit.respondentName.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="hasInterpreter"    label="rawVisit.hasInterpreter.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="interpreterName"    label="rawVisit.interpreterName.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="nonVisitedMembers"    label="rawVisit.nonVisitedMembers.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="gpsLat"    label="rawVisit.gpsLat.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="gpsLon"    label="rawVisit.gpsLon.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="gpsAlt"    label="rawVisit.gpsAlt.label" mode="${mode}" />
            <bi:field bean="${this.rawVisit}" property="gpsAcc"    label="rawVisit.gpsAcc.label" mode="${mode}" />

            <bi:field bean="${this.rawVisit}" property="collectedDate"    label="rawVisit.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawVisit}" property="uploadedDate"    label="rawVisit.uploadedDate.label" mode="show" />

        </fieldset>
        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateVisit" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateVisit" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateVisit" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteVisit" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
            </g:if>
            <g:else>
                <g:link class="edit" action="editVisit" id="${this.rawVisit.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
