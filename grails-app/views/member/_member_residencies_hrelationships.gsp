<tb:toast id="toast_mrh_msg" title="Info" message="Test message" position="top_center" />

<g:message code="rawDomain.helpers.member.label" /> : <b>${member_code}</b><br>
<g:message code="rawDomain.helpers.member.name.label" /> : <b>${member_name}</b><br>
<g:message code="rawDomain.helpers.member.gender.label" /> : <b>${g.message(code: member_gender)}</b><br>
<g:message code="rawDomain.helpers.member.dob.label" /> : <b>${member_dob}</b><br>
<h2><g:message code="rawDomain.helpers.member.residencies.label" /></h2>

<tb:tabulator id="memberResidenciesTable" name="memberResidenciesTable" toastid="toast_mrh_msg" contextMenu="true" paginationSize="5"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMemberResidenciesList', id: member_code)}"
              update="${g.createLink(controller: 'rawDomain', action: 'updateResidencyField')}">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.residency.add.label')}" type="add" action="${createLink(controller: "rawDomain", action: "createResidencyRecord")}"/>
        <tb:menu label="${message(code: 'member.residency.add.headrelationship.label')}" type="add" add_to_table="memberRelationshipsTable" action="${createLink(controller: "rawDomain", action: "createHeadRelationshipFromResidency")}"/>
        <tb:menu label="${message(code: 'member.residency.remove.label')}" type="remove" confirmDialog="default.button.delete.record.confirm.message" action="${createLink(controller: "rawDomain", action: "deleteResidencyRecord")}" />
        <tb:menu label="${message(code: 'member.residency.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableResidencyEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.residency.ignore.label')}" confirmDialog="Are you sure you want to do it?" action="${createLink(controller: "rawDomain", action: "disableResidency")}" type="update"/>
    </tb:menuBar>

    <tb:column name="household" label="${message(code: 'member.household.label')}" editor="autocomplete" editorOptions="Household.code" />
    <tb:column name="startType" label="${message(code: 'member.startType.label')}" editor="list" editorOptions="ResidencyStartType" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" editor="date" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" editor="list" editorOptions="ResidencyEndType" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" editor="date" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross" />
</tb:tabulator>

<h2><g:message code="rawDomain.helpers.member.headrelationships.label" /></h2>
<tb:tabulator id="memberRelationshipsTable" name="memberRelationshipsTable" toastid="toast_mrh_msg" contextMenu="true" paginationSize="5"
              data="${createLink(controller: 'rawDomain', action: 'fetchMemberHeadRelationshipsList', id: member_code)}"
              update="${createLink(controller: 'rawDomain', action: 'updateHeadRelationshipField')}" boxed="false"  >

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.headrelationship.add.label')}" type="add" action="${createLink(controller: "rawDomain", action: "createHeadRelationshipRecord")}"/>
        <tb:menu label="${message(code: 'member.headrelationship.add.residency.label')}" type="add" add_to_table="memberResidenciesTable" action="${createLink(controller: "rawDomain", action: "createResidencyFromHeadRelationship")}"/>
        <tb:menu label="${message(code: 'member.headrelationship.remove.label')}" type="remove" confirmDialog="default.button.delete.record.confirm.message" action="${createLink(controller: "rawDomain", action: "deleteHeadRelationshipRecord")}" />
        <tb:menu label="${message(code: 'member.headrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.headrelationship.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableHeadRelationship')}" type="update"/>
    </tb:menuBar>

    <tb:column name="household"  label="${message(code: 'member.household.label')}" editor="autocomplete" editorOptions="Household.code" />
    <tb:column name="head"  label="${message(code: 'member.head.label')}" editor="autocomplete" editorOptions="Member.code" />
    <tb:column name="relationshipType"  label="${message(code: 'member.headRelationshipType.label')}" editor="list" editorOptions="HeadRelationshipType" />
    <tb:column name="startType"     label="${message(code: 'member.startType.label')}" editor="list" editorOptions="HeadRelationshipStartType" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" editor="date" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" editor="list" editorOptions="HeadRelationshipEndType" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" editor="date" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross"/>
</tb:tabulator>