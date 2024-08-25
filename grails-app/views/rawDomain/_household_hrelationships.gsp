<tb:toast id="toast_hhr_msg" title="Info" message="Test message" position="top_center" />

<g:message code="rawDomain.helpers.household.label" /> : <b>${household_code}</b><br>
<g:if test="${old_head_code}" >
    <g:message code="rawDomain.helpers.member.oldheadcode.label" /> : <b>${old_head_code}</b><br>
</g:if>
<g:if test="${old_head_code}">
    <g:message code="rawDomain.helpers.member.newheadcode.label" /> : <b>${new_head_code}</b><br>
</g:if>
<g:if test="${member_code}">
    <g:message code="rawDomain.helpers.member.label" /> : <b>${member_code}</b><br>
</g:if>

<h2><g:message code="rawDomain.helpers.member.headrelationships.current.label" /></h2>
<tb:tabulator id="householdCurrentRelationshipsTable" name="householdCurrentRelationshipsTable" toastid="toast_hhr_msg" contextMenu="true"
              data="${createLink(controller: 'rawDomain', action: 'fetchHouseholdCurrentHeadRelationshipsList', id: household_code)}" boxed="false" paginationSize="5"
              update="${createLink(controller: 'rawDomain', action: 'updateHeadRelationshipField')}">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.headrelationship.add.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.edit.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.headrelationship.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationship')}" type="update"/>
    </tb:menuBar>

    <tb:column name="code" label="${message(code: 'member.code.label')}" />
    <tb:column name="name" label="${message(code: 'member.name.label')}" />
    <tb:column name="dob" label="${message(code: 'member.dob.label')}" />
    <tb:column name="head"  label="${message(code: 'member.head.label')}" />
    <tb:column name="relationshipType"  label="${message(code: 'member.headRelationshipType.label')}" editor="list" editorOptions="HeadRelationshipType" />
    <tb:column name="startType"     label="${message(code: 'member.startType.label')}" editor="list" editorOptions="HeadRelationshipStartType" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" editor="date" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" editor="list" editorOptions="HeadRelationshipEndType" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" editor="date" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross"/>
</tb:tabulator>

<h2><g:message code="rawDomain.helpers.member.headrelationships.all.label" /></h2>
<tb:tabulator id="householdAllRelationshipsTable" name="householdAllRelationshipsTable" toastid="toast_hhr_msg" contextMenu="true" paginationSize="5"
              data="${createLink(controller: 'rawDomain', action: 'fetchHouseholdAllHeadRelationshipsList', id: household_code)}" boxed="false"
              update="${createLink(controller: 'rawDomain', action: 'updateHeadRelationshipField')}">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.headrelationship.add.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.edit.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.headrelationship.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationship')}" type="update"/>
    </tb:menuBar>

    <tb:column name="code" label="${message(code: 'member.code.label')}" />
    <tb:column name="name" label="${message(code: 'member.name.label')}" />
    <tb:column name="dob" label="${message(code: 'member.dob.label')}" />
    <tb:column name="head"  label="${message(code: 'member.head.label')}" />
    <tb:column name="relationshipType"  label="${message(code: 'member.headRelationshipType.label')}" editor="list" editorOptions="HeadRelationshipType" />
    <tb:column name="startType"     label="${message(code: 'member.startType.label')}" editor="list" editorOptions="HeadRelationshipStartType" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" editor="date" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" editor="list" editorOptions="HeadRelationshipEndType" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" editor="date" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross"/>
</tb:tabulator>