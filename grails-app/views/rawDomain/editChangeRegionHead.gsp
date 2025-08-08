<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawChangeRegionHead.label', default: 'RawChangeRegionHead')}" />
    <title><g:message code="rawChangeRegionHead.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawChangeRegionHead" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawChangeRegionHead" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawChangeRegionHead.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawChangeRegionHead}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawChangeRegionHead}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawChangeRegionHead?.id}" />
        <g:hiddenField name="version" value="${this.rawChangeRegionHead?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawChangeRegionHead}" property="id"    label="rawChangeRegionHead.id.label" mode="show" />
            <bi:field bean="${this.rawChangeRegionHead}" property="visitCode"    label="rawChangeRegionHead.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeRegionHead}" property="regionCode"    label="rawChangeRegionHead.regionCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeRegionHead}" property="oldHeadCode"    label="rawChangeRegionHead.oldHeadCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeRegionHead}" property="newHeadCode"    label="rawChangeRegionHead.newHeadCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawChangeRegionHead}" property="eventDate"    label="rawChangeRegionHead.eventDate.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeRegionHead}" property="reason"    label="rawChangeRegionHead.reason.label" mode="${mode}" />

            <bi:field bean="${this.rawChangeRegionHead}" property="collectedBy"    label="rawChangeRegionHead.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawChangeRegionHead}" property="collectedDate"    label="rawChangeRegionHead.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawChangeRegionHead}" property="uploadedDate"    label="rawChangeRegionHead.uploadedDate.label" mode="show" />


        </fieldset>

        <g:set var="region_code" value="${this.rawChangeRegionHead.regionCode}" />
        <g:set var="old_head_code" value="${this.rawChangeRegionHead.oldHeadCode}" />
        <g:set var="new_head_code" value="${this.rawChangeRegionHead.newHeadCode}" />
        <g:set var="region_hrelationships_title" value="${message(code: 'rawDomain.helpers.changeheadrelationships.title.label')}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateChangeRegionHead" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateChangeRegionHead" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateChangeRegionHead" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteChangeRegionHead" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_region_hrelationships">
                    <g:message code="rawDomain.helpers.button.region.headrelationships.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_region_hrelationships"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editChangeRegionHead" id="${this.rawChangeRegionHead.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
