<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawChangeHead.label', default: 'RawChangeHead')}" />
    <title><g:message code="rawChangeHead.${mode}.label" args="[entityName]" /></title>

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
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawChangeHead" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawChangeHead" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawChangeHead.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawChangeHead}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawChangeHead}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawChangeHead?.id}" />
        <g:hiddenField name="version" value="${this.rawChangeHead?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawChangeHead}" property="id"    label="rawChangeHead.id.label" mode="show" />
            <bi:field bean="${this.rawChangeHead}" property="visitCode"    label="rawChangeHead.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeHead}" property="householdCode"    label="rawChangeHead.householdCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeHead}" property="oldHeadCode"    label="rawChangeHead.oldHeadCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeHead}" property="newHeadCode"    label="rawChangeHead.newHeadCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawChangeHead}" property="eventDate"    label="rawChangeHead.eventDate.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeHead}" property="reason"    label="rawChangeHead.reason.label" mode="${mode}" />

            <bi:field bean="${this.rawChangeHead}" property="collectedBy"    label="rawChangeHead.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawChangeHead}" property="collectedDate"    label="rawChangeHead.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawChangeHead}" property="uploadedDate"    label="rawChangeHead.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawChangeHead}" property="processedStatus"    label="rawChangeHead.processedStatus.label" mode="show" valueMessage="true"/>


        </fieldset>

        <g:set var="household_code" value="${this.rawChangeHead.householdCode}" />
        <g:set var="old_head_code" value="${this.rawChangeHead.oldHeadCode}" />
        <g:set var="new_head_code" value="${this.rawChangeHead.newHeadCode}" />
        <g:set var="household_hrelationships_title" value="${message(code: 'rawDomain.helpers.changeheadrelationships.title.label')}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateChangeHead" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateChangeHead" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateChangeHead" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteChangeHead" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_hrelationships">
                    <g:message code="rawDomain.helpers.button.headrelationships.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_household_hrelationships"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editChangeHead" id="${this.rawChangeHead.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
