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

            <bi:field bean="${this.rawInMigration}" property="visitCode"    label="rawInMigration.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="memberCode"    label="rawInMigration.memberCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="headRelationshipType"    label="rawInMigration.headRelationshipType.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="migrationType"    label="rawInMigration.migrationType.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="extMigrationType"    label="rawInMigration.extMigrationType.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="originCode"    label="rawInMigration.originCode.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="originOther"    label="rawInMigration.originOther.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="destinationCode"    label="rawInMigration.destinationCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawInMigration}" property="migrationDate"    label="rawInMigration.migrationDate.label" mode="${mode}" />
            <bi:field bean="${this.rawInMigration}" property="migrationReason"    label="rawInMigration.migrationReason.label" mode="${mode}" />

        </fieldset>

        <g:set var="household_code" value="${this.rawInMigration.visitCode?.replaceAll('-.+','')}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateInMigration" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateInMigration" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateInMigration" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_residencies">
                    <g:message code="rawDomain.helpers.button.residencies.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_head_relationships">
                    <g:message code="rawDomain.helpers.button.headrelationships.label" />
                </button>

                <g:render template="show_residents"/>

                <g:render template="show_head_relationships"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editInMigration" id="${this.rawInMigration.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
