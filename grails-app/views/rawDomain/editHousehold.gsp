<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawHousehold.label', default: 'RawHousehold')}" />
    <title><g:message code="rawHousehold.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawHousehold" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawHousehold" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawHousehold.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawHousehold}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawHousehold}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawHousehold?.id}" />
        <g:hiddenField name="version" value="${this.rawHousehold?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawHousehold}" property="regionCode"    label="rawHousehold.regionCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="regionName"    label="rawHousehold.regionName.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="householdCode" label="rawHousehold.householdCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="householdName" label="rawHousehold.householdName.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="headCode"      label="rawHousehold.headCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="headName"      label="rawHousehold.headName.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="gpsLat"        label="rawHousehold.gpsLat.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="gpsLon"        label="rawHousehold.gpsLon.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="gpsAlt"        label="rawHousehold.gpsAlt.label" mode="${mode}" />
            <bi:field bean="${this.rawHousehold}" property="gpsAcc"        label="rawHousehold.gpsAcc.label" mode="${mode}" />

        </fieldset>
        <fieldset class="buttons">
            <g:if test="${mode=="edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateHousehold" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateHousehold" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateHousehold" />
            </g:if>
            <g:else>
                <g:link class="edit" action="editHousehold" id="${this.rawHousehold.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
