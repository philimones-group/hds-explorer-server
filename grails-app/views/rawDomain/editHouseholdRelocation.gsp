<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawHouseholdRelocation.label', default: 'RawHouseholdRelocation')}" />
    <title><g:message code="rawHouseholdRelocation.${mode}.label" args="[entityName]" /></title>

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
    <tb:tabulatorResources/>
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

<a href="#${mode}-rawHouseholdRelocation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawHouseholdRelocation" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawHouseholdRelocation.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawHouseholdRelocation}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawHouseholdRelocation}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawHouseholdRelocation?.id}" />
        <g:hiddenField name="version" value="${this.rawHouseholdRelocation?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawHouseholdRelocation}" property="id"    label="rawHouseholdRelocation.id.label" mode="show" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="visitCode"    label="rawHouseholdRelocation.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="originCode"    label="rawHouseholdRelocation.originCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="destinationCode"    label="rawHouseholdRelocation.destinationCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="headCode"    label="rawHouseholdRelocation.headCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawHouseholdRelocation}" property="eventDate"    label="rawHouseholdRelocation.eventDate.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="reason"    label="rawHouseholdRelocation.reason.label" mode="${mode}" options="HouseholdRelocationReason" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="reasonOther"    label="rawHouseholdRelocation.reasonOther.label" mode="${mode}" />

            <bi:field bean="${this.rawHouseholdRelocation}" property="collectedBy"    label="rawHouseholdRelocation.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="collectedDate"    label="rawHouseholdRelocation.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="uploadedDate"    label="rawHouseholdRelocation.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawHouseholdRelocation}" property="processedStatus"    label="rawHouseholdRelocation.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawHouseholdRelocation.originCode}" />
        <g:set var="household_b_code" value="${this.rawHouseholdRelocation.destinationCode}" />
        <g:set var="household_residents_title" value="${message(code: 'rawDomain.helpers.householdrelocations.title.label')}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateHouseholdRelocation" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateHouseholdRelocation" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateHouseholdRelocation" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteHouseholdRelocation" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.householdrelocations.show.origin.residents.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_b_residents">
                    <g:message code="rawDomain.helpers.householdrelocations.show.destination.residents.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_household_residents"/>

                <g:render template="show_household_b_residents"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editHouseholdRelocation" id="${this.rawHouseholdRelocation.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
