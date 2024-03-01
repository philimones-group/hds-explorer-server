<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawMemberEnu.label', default: 'RawMemberEnu')}" />
    <title><g:message code="rawMemberEnu.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawMemberEnu" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawMemberEnu" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawMemberEnu.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawMemberEnu}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawMemberEnu}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawMemberEnu?.id}" />
        <g:hiddenField name="version" value="${this.rawMemberEnu?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawMemberEnu}" property="visitCode"    label="rawMemberEnu.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="code"    label="rawMemberEnu.code.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="name"    label="rawMemberEnu.name.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="gender"    label="rawMemberEnu.gender.label" mode="${mode}" />
            <bi:dateField bean="${this.rawMemberEnu}" property="dob"    label="rawMemberEnu.dob.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="motherCode"    label="rawMemberEnu.motherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="motherName"    label="rawMemberEnu.motherName.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="fatherCode"    label="rawMemberEnu.fatherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="fatherName"    label="rawMemberEnu.fatherName.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="householdCode"    label="rawMemberEnu.householdCode.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="householdName"    label="rawMemberEnu.householdName.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="headRelationshipType"    label="rawMemberEnu.headRelationshipType.label" mode="${mode}" />
            <bi:dateField bean="${this.rawMemberEnu}" property="residencyStartDate"    label="rawMemberEnu.residencyStartDate.label" mode="${mode}" />
            <bi:field bean="${this.rawMemberEnu}" property="modules"    label="rawMemberEnu.modules.label" mode="${mode}" />

        </fieldset>
        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateMemberEnu" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateMemberEnu" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateMemberEnu" />
            </g:if>
            <g:else>
                <g:link class="edit" action="editMemberEnu" id="${this.rawMemberEnu.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
