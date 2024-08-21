<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawOutMigration.label', default: 'RawOutMigration')}" />
    <title><g:message code="rawOutMigration.${mode}.label" args="[entityName]" /></title>

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
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawOutMigration" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawOutMigration" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawOutMigration.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawOutMigration}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawOutMigration}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawOutMigration?.id}" />
        <g:hiddenField name="version" value="${this.rawOutMigration?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawOutMigration}" property="id"    label="rawOutMigration.id.label" mode="show" />
            <bi:field bean="${this.rawOutMigration}" property="visitCode"    label="rawOutMigration.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="memberCode"    label="rawOutMigration.memberCode.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="migrationType"    label="rawOutMigration.migrationType.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="originCode"    label="rawOutMigration.originCode.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="destinationCode"    label="rawOutMigration.destinationCode.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="destinationOther"    label="rawOutMigration.destinationOther.label" mode="${mode}" />
            <bi:dateField bean="${this.rawOutMigration}" property="migrationDate"    label="rawOutMigration.migrationDate.label" mode="${mode}" />
            <bi:field bean="${this.rawOutMigration}" property="migrationReason"    label="rawOutMigration.migrationReason.label" mode="${mode}" />

            <bi:field bean="${this.rawOutMigration}" property="collectedDate"    label="rawOutMigration.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawOutMigration}" property="uploadedDate"    label="rawOutMigration.uploadedDate.label" mode="show" />

        </fieldset>

        <g:set var="household_code" value="${this.rawOutMigration.visitCode?.replaceAll('-.+','')}" />
        <g:set var="member_code" value="${member?.code}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateOutMigration" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateOutMigration" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateOutMigration" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteOutmigration" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <g:render template="show_member_residencies_hrelationships"/>

                <g:render template="show_household_residents"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editOutMigration" id="${this.rawOutMigration.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
