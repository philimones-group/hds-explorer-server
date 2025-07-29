<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawPregnancyVisit.label', default: 'RawPregnancyVisit')}" />
    <title><g:message code="rawPregnancyVisit.${mode}.label" args="[entityName]" /></title>

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

<a href="#${mode}-rawPregnancyVisit" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawPregnancyVisit" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawPregnancyVisit.${mode}.label" args="[entityName]" /></h1>
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

    <g:hasErrors bean="${this.rawPregnancyVisit}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawPregnancyVisit}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawPregnancyVisit?.id}" />
        <g:hiddenField name="version" value="${this.rawPregnancyVisit?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawPregnancyVisit}" property="id" label="rawPregnancyVisit.id.label" mode="show" />
            <bi:field bean="${this.rawPregnancyVisit}" property="code" label="rawPregnancyVisit.code.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="motherCode" label="rawPregnancyVisit.motherCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="visitCode" label="rawPregnancyVisit.visitCode.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="status" label="rawPregnancyVisit.status.label" mode="${mode}" options="PregnancyStatus" />
            <bi:field bean="${this.rawPregnancyVisit}" property="visitType" label="rawPregnancyVisit.visitType.label" mode="${mode}" options="PregnancyVisitType" />
            <bi:field bean="${this.rawPregnancyVisit}" property="visitNumber" label="rawPregnancyVisit.visitNumber.label" mode="${mode}" />
            <bi:dateField bean="${this.rawPregnancyVisit}" property="visitDate" label="rawPregnancyVisit.visitDate.label" mode="${mode}" />

            <!-- Antepartum Fields -->
            <bi:field bean="${this.rawPregnancyVisit}" property="weeksGestation" label="rawPregnancyVisit.weeksGestation.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="prenatalCareReceived" label="rawPregnancyVisit.prenatalCareReceived.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="prenatalCareProvider" label="rawPregnancyVisit.prenatalCareProvider.label" mode="${mode}" options="HealthcareProviderType" nullable="true" />
            <bi:field bean="${this.rawPregnancyVisit}" property="complicationsReported" label="rawPregnancyVisit.complicationsReported.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="complicationDetails" label="rawPregnancyVisit.complicationDetails.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="hasBirthPlan" label="rawPregnancyVisit.hasBirthPlan.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="expectedBirthPlace" label="rawPregnancyVisit.expectedBirthPlace.label" mode="${mode}" options="BirthPlace" nullable="true" />
            <bi:field bean="${this.rawPregnancyVisit}" property="birthPlaceOther" label="rawPregnancyVisit.birthPlaceOther.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="transportationPlan" label="rawPregnancyVisit.transportationPlan.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="financialPreparedness" label="rawPregnancyVisit.financialPreparedness.label" mode="${mode}" />

        <!-- Postpartum Fields -->
            <bi:field bean="${this.rawPregnancyVisit}" property="postpartumComplications" label="rawPregnancyVisit.postpartumComplications.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="postpartumComplicationDetails" label="rawPregnancyVisit.postpartumComplicationDetails.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="breastfeedingStatus" label="rawPregnancyVisit.breastfeedingStatus.label" mode="${mode}" options="BreastFeedingStatus" />
            <bi:field bean="${this.rawPregnancyVisit}" property="resumedDailyActivities" label="rawPregnancyVisit.resumedDailyActivities.label" mode="${mode}" />
            <bi:field bean="${this.rawPregnancyVisit}" property="attendedPostpartumCheckup" label="rawPregnancyVisit.attendedPostpartumCheckup.label" mode="${mode}" />

            <bi:field bean="${this.rawPregnancyVisit}" property="modules"    label="rawPregnancyVisit.modules.label" mode="${mode}" />

            <g:if test="${this.rawPregnancyVisit?.childs}">

                <li class="fieldcontain">
                    <span id="relationships-label" class="property-label">
                        <g:message code="rawPregnancyVisit.childs.label" />
                    </span>
                    <span class="property-value" aria-labelledby="childs-label">
                        <ul>
                            <g:each in="${this.rawPregnancyVisit.childs}" var="child">
                                <li class="list-style-type: square;">
                                    <g:link controller="rawDomain" action="editPregnancyVisitChild" id="${child.id}">
                                        ${child?.toString()}
                                    </g:link>
                                </li>
                            </g:each>
                        </ul>
                    </span>
                </li>

            </g:if>

            <bi:field bean="${this.rawPregnancyVisit}" property="collectedBy"    label="rawPregnancyVisit.collectedBy.label" mode="show" />
            <bi:field bean="${this.rawPregnancyVisit}" property="collectedDate"    label="rawPregnancyVisit.collectedDate.label" mode="show" />
            <bi:field bean="${this.rawPregnancyVisit}" property="uploadedDate"    label="rawPregnancyVisit.uploadedDate.label" mode="show" />
            <bi:field bean="${this.rawPregnancyVisit}" property="processedStatus"    label="rawPregnancyVisit.processedStatus.label" mode="show" valueMessage="true"/>

        </fieldset>

        <g:set var="household_code" value="${this.rawPregnancyVisit.visitCode?.replaceAll('-.+','')}" />
        <g:set var="household_name" value="${member?.householdName}" />
        <g:set var="member_code" value="${this.rawPregnancyVisit.motherCode}" />
        <g:set var="member_name" value="${member?.name}" />
        <g:set var="member_gender" value="${member?.gender}" />
        <g:set var="member_dob" value="${member?.dob}" />

        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.reset.label")}" action="updatePregnancyVisit" onclick="updateReset('true')" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updatePregnancyVisit" />
                <g:actionSubmit class="save" value="${message(code: "rawDomain.invalidate.label")}" action="invalidatePregnancyVisit" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deletePregnancyVisit" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_residencies_hrelationships">
                    <g:message code="rawDomain.helpers.button.member.residencies_and_headrelationships.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_household_residents">
                    <g:message code="rawDomain.helpers.button.household.residents.label" />
                </button>

                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#show_member_pregnancies">
                    <g:message code="rawDomain.helpers.button.show.member.pregnancies.label" />
                </button>

                <g:if test="${dependencyResult.hasDependencyError==true}">
                    <g:hiddenField name="dependencyEventId" value="${dependencyResult.dependencyEventId}" />

                    <button type="submit" class="btn btn-primary" data-toggle="button" >
                        <g:message code="rawDomain.helpers.button.show.dependency.label" />
                    </button>
                </g:if>

                <g:render template="show_member_residencies_hrelationships"/>

                <g:render template="show_household_residents"/>

                <g:render template="show_member_pregnancies"/>

            </g:if>
            <g:else>
                <g:link class="edit" action="editPregnancyVisit" id="${this.rawPregnancyVisit.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
