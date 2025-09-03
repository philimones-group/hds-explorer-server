<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawHouseholdProxyHead.label', default: 'RawHouseholdProxyHead')}" />
    <title><g:message code="rawHouseholdProxyHead.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawHouseholdProxyHead" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawHouseholdProxyHead" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawHouseholdProxyHead.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawHouseholdProxyHead}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawHouseholdProxyHead}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawHouseholdProxyHead?.id}" />
        <g:hiddenField name="version" value="${this.rawHouseholdProxyHead?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawHouseholdProxyHead}" property="id"    label="rawHouseholdProxyHead.id.label" mode="show" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="visitCode"    label="rawHouseholdProxyHead.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="householdCode"    label="rawHouseholdProxyHead.householdCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="proxyHeadType"    label="rawHouseholdProxyHead.proxyHeadType.label" mode="${mode}" options="ProxyHeadType" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="proxyHeadCode"    label="rawHouseholdProxyHead.proxyHeadCode.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="proxyHeadName"    label="rawHouseholdProxyHead.proxyHeadName.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="proxyHeadRole"    label="rawHouseholdProxyHead.proxyHeadRole.label" mode="${mode}" options="ProxyHeadRole" />
            <bi:dateField bean="${this.rawHouseholdProxyHead}" property="eventDate"    label="rawHouseholdProxyHead.eventDate.label" mode="${mode}" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="reason"    label="rawHouseholdProxyHead.reason.label" mode="${mode}" options="ProxyHeadChangeReason" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="reasonOther"    label="rawHouseholdProxyHead.reasonOther.label" mode="${mode}" />

            <g:if test="${this.rawHouseholdProxyHead.extensionForm}">
                <div class="fieldcontain  d-flex align-items-center">
                    <span id="extensionForm-label" class="property-label me-2">
                        <g:message code="rawDomain.helpers.show.xml.instance.property.label" />
                    </span>
                    <span class="property-valuex" aria-labelledby="extensionForm-label">
                        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_xml_instance">
                            <g:message code="rawDomain.helpers.show.xml.instance.label" />
                        </button>
                    </span>
                </div>
            </g:if>

            <bi:field bean="${this.rawHouseholdProxyHead}" property="collectedBy"    label="rawHouseholdProxyHead.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="collectedDate"    label="rawHouseholdProxyHead.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawHouseholdProxyHead}" property="uploadedDate"    label="rawHouseholdProxyHead.uploadedDate.label" mode="show" />


        </fieldset>

        <g:set var="household_code" value="${this.rawHouseholdProxyHead.householdCode}" />
        <g:set var="xmlInstance" value="${net.betainteractive.io.odk.util.XFormReader.formatXmlPretty(this.rawHouseholdProxyHead.extensionForm)}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateHouseholdProxyHead" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateHouseholdProxyHead" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateHouseholdProxyHead" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteHouseholdProxyHead" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_household_residents"/>

                <g:render template="show_xml_instance"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editHouseholdProxyHead" id="${this.rawHouseholdProxyHead.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
