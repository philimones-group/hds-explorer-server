<tb:toast id="toast_b_msg" title="Info" message="Test message" position="top_center" />

<g:message code="rawDomain.helpers.member.b.label" /> : <b>${member_b_code}</b><br>
<g:message code="rawDomain.helpers.member.a.label" /> : <b>${member_a_code}</b><br>
<br>
<h2><g:message code="rawDomain.helpers.maritalrelationships.title.label" /></h2>

<tb:tabulator id="memberBRelationshipsTable" name="memberBRelationshipsTable" toastid="toast_b_msg" contextMenu="true"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMaritalRelationshipsList', id: member_b_code)}" paginationSize="5"
              update="${createLink(controller: 'rawDomain', action: 'updateMaritalRelationshipField')}">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.maritalrelationship.remove.label')}" type="remove" confirmDialog="default.button.delete.record.confirm.message" action="${createLink(controller: "rawDomain", action: "deleteMaritalRelationshipRecord")}" />
        <tb:menu label="${message(code: 'member.maritalrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableMaritalRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.maritalrelationship.ignore.label')}" action="${createLink(controller: "rawDomain", action: "disableMaritalRelationship")}" type="update"/>
    </tb:menuBar>

    <tb:column name="memberA_code" label="${message(code: 'rawMaritalRelationship.memberA.label')}" />
    <tb:column name="memberB_code" label="${message(code: 'rawMaritalRelationship.memberB.label')}" />
    <tb:column name="isPolygamic" label="${message(code: 'rawMaritalRelationship.isPolygamic.label')}" editor="tickCross" formatter="tickCross" />
    <tb:column name="startStatus" label="${message(code: 'rawMaritalRelationship.startStatus.label')}" editor="list" editorOptions="MaritalStartStatus"/>
    <tb:column name="startDate" label="${message(code: 'rawMaritalRelationship.startDate.label')}" editor="date" />
    <tb:column name="endStatus" label="${message(code: 'rawMaritalRelationship.endStatus.label')}" editor="list" editorOptions="MaritalEndStatus" />
    <tb:column name="endDate" label="${message(code: 'rawMaritalRelationship.endDate.label')}" editor="date" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross" />
</tb:tabulator>