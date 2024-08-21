<g:message code="rawDomain.helpers.member.label" /> : <b>${member_code}</b><br>
<g:message code="rawDomain.helpers.member.name.label" /> : <b>${member_name}</b><br>
<g:message code="rawDomain.helpers.member.gender.label" /> : <b>${member_gender}</b><br>
<g:message code="rawDomain.helpers.member.dob.label" /> : <b>${member_dob}</b><br>
<h2><g:message code="rawDomain.helpers.member.residencies.label" /></h2>

<tb:toast id="toast_msg" title="Info" message="Test message" />

<tb:tabulator id="memberResidenciesTable" name="memberResidenciesTable" toastid="toast_msg" contextMenu="true"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMemberResidenciesList', id: member_code)}" paginationSize="5">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.residency.add.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.residency.edit.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.residency.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableResidencyEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.residency.ignore.label')}" action="${createLink(controller: "rawDomain", action: "disableResidency")}" type="update"/>
    </tb:menuBar>

    <tb:column name="household" label="${message(code: 'member.household.label')}" />
    <tb:column name="startType" label="${message(code: 'member.startType.label')}" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross" />
</tb:tabulator>

<h2><g:message code="rawDomain.helpers.member.headrelationships.label" /></h2>
<tb:tabulator id="memberRelationshipsTable" name="memberRelationshipsTable" toastid="toast_msg" contextMenu="true"
              data="${createLink(controller: 'rawDomain', action: 'fetchMemberHeadRelationshipsList', id: member_code)}"
              update="${createLink(controller: 'rawDomain', action: 'updateHeadRelationship')}" boxed="false"  paginationSize="5">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.headrelationship.add.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.edit.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.headrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.headrelationship.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationship')}" type="update"/>
    </tb:menuBar>

    <tb:column name="household"  label="${message(code: 'member.household.label')}" />
    <tb:column name="head"  label="${message(code: 'member.head.label')}" />
    <tb:column name="headRelationshipType"  label="${message(code: 'member.headRelationshipType.label')}" />
    <tb:column name="startType"     label="${message(code: 'member.startType.label')}" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross"/>
</tb:tabulator>