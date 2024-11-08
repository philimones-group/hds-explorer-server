<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'member.label', default: 'Member')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>

        <tb:tabulatorResources/>
        <tb:luxonResources/>
        <tb:jquiResources/>
    </head>
    <body>
        <a href="#show-member" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
            </ul>
        </div>
        <div id="show-member" class="content scaffold-show" role="main">
            <h1><g:message code="member.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>


            <bi:field bean="${this.member}" property="code"  label="member.code.label" mode="show" />
            <bi:field bean="${this.member}" property="name" label="member.name.label" mode="show" />
            <bi:field bean="${this.member}" property="gender" label="member.gender.label" mode="show" valueMessage="true" />
            <bi:field bean="${this.member}" property="dob" label="member.dob.label" mode="show" />
            <bi:field bean="${this.member}" property="motherCode" label="member.motherCode.label" mode="show" />
            <bi:field bean="${this.member}" property="motherName" label="member.motherName.label" mode="show" valueMessage="true" />
            <bi:field bean="${this.member}" property="fatherCode" label="member.fatherCode.label" mode="show" />
            <bi:field bean="${this.member}" property="fatherName" label="member.fatherName.label" mode="show" valueMessage="true" />
            <bi:field bean="${this.member}" property="maritalStatus" label="member.maritalStatus.label" mode="show" valueMessage="true" />
            <bi:field bean="${this.member}" property="spouseCode" label="member.spouseCode.label" mode="show" />
            <bi:field bean="${this.member}" property="spouseName" label="member.spouseName.label" mode="show" />
            <bi:field bean="${this.member}" property="householdCode" label="member.householdCode.label" mode="show" />
            <bi:field bean="${this.member}" property="householdName" label="member.householdName.label" mode="show" />
            <bi:field bean="${this.member}" property="headRelationshipType" label="member.headRelationshipType.label" mode="show" valueMessage="true" />
            <bi:field bean="${this.member}" property="startType" label="member.startType.label" mode="show" valueMessage="true" />
            <bi:dateField bean="${this.member}" property="startDate" label="member.startDate.label" mode="show" />
            <bi:field bean="${this.member}" property="endType" label="member.endType.label" mode="show" valueMessage="true" />
            <bi:dateField bean="${this.member}" property="endDate" label="member.endDate.label" mode="show" />
            <bi:field bean="${this.member}" property="entryHousehold" label="member.entryHousehold.label" mode="show" />
            <bi:field bean="${this.member}" property="entryType" label="member.entryType.label" mode="show" valueMessage="true" />
            <bi:dateField bean="${this.member}" property="entryDate" label="member.entryDate.label" mode="show" />
            <bi:field bean="${this.member}" property="createdBy" label="member.createdBy.label" mode="show" />
            <bi:dateField bean="${this.member}" property="createdDate" label="member.createdDate.label" mode="show" />
            <bi:field bean="${this.member}" property="updatedBy" label="member.updatedBy.label" mode="show" />
            <bi:dateField bean="${this.member}" property="updatedDate" label="member.updatedDate.label" mode="show" />


            <g:set var="member_code" value="${this.member?.code}" />
            <g:set var="member_name" value="${this.member?.name}" />
            <g:set var="member_gender" value="${this.member?.gender}" />
            <g:set var="member_dob" value="${this.member?.dob}" />

            <g:form resource="${this.member}" method="DELETE">
                <fieldset class="buttons">
                    <!-- <g:link class="edit" action="edit" resource="${this.member}"><g:message code="default.button.edit.label" default="Edit" /></g:link> -->
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                    <input class="list" type="button" data-toggle="modal" data-target="#show_member_residencies_hrelationships" value="${message(code: 'rawDomain.helpers.button.member.residencies_and_headrelationships.label', default: 'Show Residents')}" />

                    <g:render template="show_member_residencies_hrelationships"/>

                </fieldset>
            </g:form>
        </div>
    </body>
</html>
