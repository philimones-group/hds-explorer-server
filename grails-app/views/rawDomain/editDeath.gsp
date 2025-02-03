<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawDeath.label', default: 'RawDeath')}" />
    <title><g:message code="rawDeath.${mode}.label" args="[entityName]" /></title>

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
    <tb:jquiResources/>
</head>
<body>

<g:javascript>
    function updateReset(value) {
        $("#reset").val(value);
    }
</g:javascript>

<a href="#${mode}-rawDeath" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawDeath" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawDeath.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawDeath}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawDeath}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawDeath?.id}" />
        <g:hiddenField name="version" value="${this.rawDeath?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawDeath}" property="id"    label="rawDeath.id.label" mode="show" />
            <bi:field bean="${this.rawDeath}" property="visitCode"    label="rawDeath.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawDeath}" property="memberCode"    label="rawDeath.memberCode.label" mode="${mode}" />
            <bi:dateField bean="${this.rawDeath}" property="deathDate"    label="rawDeath.deathDate.label" mode="${mode}" />
            <bi:field bean="${this.rawDeath}" property="deathCause"    label="rawDeath.deathCause.label" mode="${mode}" />
            <bi:field bean="${this.rawDeath}" property="deathPlace"    label="rawDeath.deathPlace.label" mode="${mode}" />

            <g:if test="${this.rawDeath?.relationships}">

                <li class="fieldcontain">
                    <span id="relationships-label" class="property-label">
                        <g:message code="rawDeath.relationships.label" />
                    </span>
                    <span class="property-value" aria-labelledby="relationships-label">
                        <ul>
                            <g:each in="${this.rawDeath.relationships}" var="deathRelationship">
                                <li class="list-style-type: square;">
                                    <g:link controller="rawDomain" action="editDeathRelationship" id="${deathRelationship.id}">
                                        ${deathRelationship?.toString()}
                                    </g:link>

                                    <g:form controller="rawDomain" method="POST" style="display:inline;" >
                                        <g:hiddenField name="id" value="${deathRelationship.id}" />
                                        <g:actionSubmit class="btn btn-outline-primary" value="${message(code: 'default.button.delete.label')}" action="deleteDeathRelationship" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" style="margin-left: 10px;" />
                                    </g:form>
                                </li>
                            </g:each>
                        </ul>
                    </span>
                </li>

            </g:if>

            <bi:field bean="${this.rawDeath}" property="collectedBy"    label="rawDeath.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawDeath}" property="collectedDate"    label="rawDeath.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawDeath}" property="uploadedDate"    label="rawDeath.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawDeath}" property="processedStatus"    label="rawDeath.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawDeath.visitCode?.replaceAll('-.+','')}" />
        <g:set var="member_code" value="${member?.code}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />
        <g:set var="member_a_code" value="${member?.code}" />
        <g:set var="household_hrelationships_title" value="${message(code: 'rawDomain.helpers.deathheadrelationships.title.label')}" />


        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updateDeath" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateDeath" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidateDeath" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteDeath" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
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

                <g:render template="show_household_hrelationships"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editDeath" id="${this.rawDeath.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
