<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawExternalInMigration.label', default: 'RawExternalInMigration')}" />
    <title><g:message code="rawExternalInMigration.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawExternalInMigration" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawExternalInMigration" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawExternalInMigration.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawExternalInMigration}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawExternalInMigration}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawExternalInMigration?.id}" />
        <g:hiddenField name="version" value="${this.rawExternalInMigration?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawExternalInMigration}" property="id"    label="rawExternalInMigration.id.label" mode="show" />
            <bi:field bean="${this.rawExternalInMigration}" property="visitCode"    label="rawExternalInMigration.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="memberCode"    label="rawExternalInMigration.memberCode.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="memberName"    label="rawExternalInMigration.memberName.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="memberGender"    label="rawExternalInMigration.memberGender.label" mode="${mode}" options="Gender" />
            <bi:dateField bean="${this.rawExternalInMigration}" property="memberDob"    label="rawExternalInMigration.memberDob.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="memberMotherCode"    label="rawExternalInMigration.memberMotherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="memberFatherCode"    label="rawExternalInMigration.memberFatherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="headRelationshipType"    label="rawExternalInMigration.headRelationshipType.label" mode="${mode}" options="HeadRelationshipType" />
            <bi:field bean="${this.rawExternalInMigration}" property="migrationType"    label="rawExternalInMigration.migrationType.label" mode="${mode}" options="InMigrationType" />
            <bi:field bean="${this.rawExternalInMigration}" property="extMigrationType"    label="rawExternalInMigration.extMigrationType.label" mode="${mode}" options="ExternalInMigrationType" />
            <bi:field bean="${this.rawExternalInMigration}" property="originCode"    label="rawExternalInMigration.originCode.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="originOther"    label="rawExternalInMigration.originOther.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="destinationCode"    label="rawExternalInMigration.destinationCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawExternalInMigration}" property="migrationDate"    label="rawExternalInMigration.migrationDate.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="migrationReason"    label="rawExternalInMigration.migrationReason.label" mode="${mode}" />
            <bi:field bean="${this.rawExternalInMigration}" property="modules"    label="rawExternalInMigration.modules.label" mode="${mode}" />

            <bi:field bean="${this.rawExternalInMigration}" property="collectedBy"    label="rawExternalInMigration.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawExternalInMigration}" property="collectedDate"    label="rawExternalInMigration.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawExternalInMigration}" property="uploadedDate"    label="rawExternalInMigration.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawExternalInMigration}" property="processedStatus"    label="rawExternalInMigration.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawExternalInMigration.visitCode?.replaceAll('-.+','')}" />
        <g:set var="household_name" value="${member?.householdName}" />
        <g:set var="member_code" value="${this.rawExternalInMigration.memberCode}" />
        <g:set var="member_name" value="${this.rawExternalInMigration?.memberName}" />
        <g:set var="member_gender" value="${this.rawExternalInMigration?.memberGender}" />
        <g:set var="member_dob" value="${bi.formatDate(date: this.rawExternalInMigration?.memberDob)}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateExternalInMigration" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateExternalInMigration" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateExternalInMigration" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteExtInmigration" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_member_residencies_hrelationships"/>

                <g:render template="show_household_residents"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editExternalInMigration" id="${this.rawExternalInMigration.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
