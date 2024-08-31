<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawInMigration.label', default: 'RawInMigration')}" />
    <title><g:message code="rawInMigration.${mode}.label" args="[entityName]" /></title>

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
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawInMigration" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawInMigration" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawInMigration.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawInMigration}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawInMigration}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawInMigration?.id}" />
        <g:hiddenField name="version" value="${this.rawInMigration?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawInMigration}" property="id"    label="rawInMigration.id.label" mode="show" />
            <bi:field bean="${this.rawInMigration}" property="visitCode"    label="rawInMigration.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="memberCode"    label="rawInMigration.memberCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="headRelationshipType"    label="rawInMigration.headRelationshipType.label" mode="${mode}" options="HeadRelationshipType" />
            <bi:field bean="${this.rawInMigration}" property="migrationType"    label="rawInMigration.migrationType.label" mode="${mode}" options="InMigrationType" />
            <bi:field bean="${this.rawInMigration}" property="extMigrationType"    label="rawInMigration.extMigrationType.label" mode="${mode}" options="ExternalInMigrationType" nullable="true" />
            <bi:field bean="${this.rawInMigration}" property="originCode"    label="rawInMigration.originCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="originOther"    label="rawInMigration.originOther.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="destinationCode"    label="rawInMigration.destinationCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawInMigration}" property="migrationDate"    label="rawInMigration.migrationDate.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="migrationReason"    label="rawInMigration.migrationReason.label" mode="${mode}" />

            <bi:field bean="${this.rawInMigration}" property="collectedBy"    label="rawInMigration.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawInMigration}" property="collectedDate"    label="rawInMigration.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawInMigration}" property="uploadedDate"    label="rawInMigration.uploadedDate.label" mode="show" />

        </fieldset>

        <g:set var="household_code" value="${this.rawInMigration.visitCode?.replaceAll('-.+','')}" />
        <g:set var="household_name" value="${member?.householdName}" />
        <g:set var="member_code" value="${member?.code}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateInMigration" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateInMigration" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateInMigration" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteInmigration" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_hrelationships">
                    <g:message code="rawDomain.helpers.button.headrelationships.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_member_residencies_hrelationships"/>

                <g:render template="show_household_residents"/>

                <g:render template="show_household_hrelationships"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editInMigration" id="${this.rawInMigration.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
