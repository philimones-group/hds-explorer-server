<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawPregnancyVisitChild.label', default: 'rawPregnancyVisitChild')}" />
    <title><g:message code="rawPregnancyVisitChild.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawPregnancyVisitChild" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawPregnancyVisitChild" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawPregnancyVisitChild.${mode}.label" default="Edit Raw Pregnancy Visit Child" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawPregnancyVisitChild}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawPregnancyVisitChild}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawPregnancyVisitChild?.id}" />
        <g:hiddenField name="version" value="${this.rawPregnancyVisitChild?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawPregnancyVisitChild}" property="id" label="rawPregnancyVisitChild.id.label" mode="show" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="pregnancyCode" label="rawPregnancyVisitChild.pregnancyCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childCode" label="rawPregnancyVisitChild.childCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="outcomeType" label="rawPregnancyVisitChild.outcomeType.label" mode="${mode}" options="PregnancyOutcomeType" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childStatus" label="rawPregnancyVisitChild.childStatus.label" mode="${mode}" options="NewBornStatus" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childWeight" label="rawPregnancyVisitChild.childWeight.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="hadIllnessSymptoms" label="rawPregnancyVisitChild.hadIllnessSymptoms.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childIllnessSymptoms" label="rawPregnancyVisitChild.childIllnessSymptoms.label" value="${symptomsList}" mode="${mode}" options="IllnessSymptoms" multiple="true" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childBreastfeedingStatus" label="rawPregnancyVisitChild.childBreastfeedingStatus.label" mode="${mode}" options="BreastFeedingStatus" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="childImmunizationStatus" label="rawPregnancyVisitChild.childImmunizationStatus.label" mode="${mode}" options="ImmunizationStatus" />
            <bi:field bean="${this.rawPregnancyVisitChild}" property="notes" label="rawPregnancyVisitChild.notes.label" mode="${mode}" />

        </fieldset>

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updatePregnancyVisitChild" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deletePregnancyVisitChild" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
            </g:if>
            <g:else>
                <g:link class="edit" action="editPregnancyVisitChild" id="${this.rawPregnancyVisitChild.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
