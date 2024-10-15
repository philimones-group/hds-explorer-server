<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawPregnancyRegistration.label', default: 'RawPregnancyRegistration')}" />
    <title><g:message code="rawPregnancyRegistration.${mode}.label" args="[entityName]" /></title>

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

    <tb:tabulatorResources />
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawPregnancyRegistration" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawPregnancyRegistration" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawPregnancyRegistration.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawPregnancyRegistration}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawPregnancyRegistration}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawPregnancyRegistration?.id}" />
        <g:hiddenField name="version" value="${this.rawPregnancyRegistration?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawPregnancyRegistration}" property="id"    label="rawPregnancyRegistration.id.label" mode="show" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="code"    label="rawPregnancyRegistration.code.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="motherCode"    label="rawPregnancyRegistration.motherCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyRegistration}" property="recordedDate"    label="rawPregnancyRegistration.recordedDate.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="pregMonths"    label="rawPregnancyRegistration.pregMonths.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="eddKnown"    label="rawPregnancyRegistration.eddKnown.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="hasPrenatalRecord"    label="rawPregnancyRegistration.hasPrenatalRecord.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyRegistration}" property="eddDate"    label="rawPregnancyRegistration.eddDate.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="eddType"    label="rawPregnancyRegistration.eddType.label" mode="${mode}" options="EstimatedDateOfDeliveryType" nullable="true" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="lmpKnown"    label="rawPregnancyRegistration.lmpKnown.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyRegistration}" property="lmpDate"    label="rawPregnancyRegistration.lmpDate.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyRegistration}" property="expectedDeliveryDate"    label="rawPregnancyRegistration.expectedDeliveryDate.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="status"    label="rawPregnancyRegistration.status.label" mode="${mode}" options="PregnancyStatus" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="visitCode"    label="rawPregnancyRegistration.visitCode.label" mode="${mode}" />

            <bi:field bean="${this.rawPregnancyRegistration}" property="collectedBy"    label="rawPregnancyRegistration.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="collectedDate"    label="rawPregnancyRegistration.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawPregnancyRegistration}" property="uploadedDate"    label="rawPregnancyRegistration.uploadedDate.label" mode="show" />

            <bi:field bean="${this.rawPregnancyRegistration}" property="processedStatus"    label="rawPregnancyRegistration.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawPregnancyRegistration.visitCode?.replaceAll('-.+','')}" />
        <g:set var="household_name" value="${member?.householdName}" />
        <g:set var="member_code" value="${this.rawPregnancyRegistration.motherCode}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updatePregnancyRegistration" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updatePregnancyRegistration" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidatePregnancyRegistration" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deletePregnancyRegistration" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_pregnancies">
                    <g:message code="rawDomain.helpers.button.show.member.pregnancies.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_member_pregnancies"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editPregnancyRegistration" id="${this.rawPregnancyRegistration.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
