<g:message code="rawDomain.helpers.member.a.label" /> : <b>${member_a_code}</b><br>

<h2><g:message code="rawDomain.helpers.maritalrelationships.title.label" /></h2>

<tb:toast id="toast_msg" title="Info" message="Test message" />

<tb:tabulator id="memberARelationshipsTable" name="memberARelationshipsTable" toastid="toast_msg" contextMenu="true" paginationSize="5"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMaritalRelationshipsList', id: member_a_code)}">

    <tb:menuBar>
        <tb:menu label="${message(code: 'member.maritalrelationship.add.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.maritalrelationship.edit.label')}" disabled="true" />
        <tb:menu label="${message(code: 'member.maritalrelationship.partial.ignore.label')}" action="${createLink(controller: 'rawDomain', action: 'disableMaritalRelationshipEndEvent')}" type="update"/>
        <tb:menu label="${message(code: 'member.maritalrelationship.ignore.label')}" action="${createLink(controller: "rawDomain", action: "disableMaritalRelationship")}" type="update"/>
    </tb:menuBar>

    <tb:column name="memberA_code" label="${message(code: 'rawMaritalRelationship.memberA.label')}" />
    <tb:column name="memberB_code" label="${message(code: 'rawMaritalRelationship.memberB.label')}" />
    <tb:column name="isPolygamic" label="${message(code: 'rawMaritalRelationship.isPolygamic.label')}" />
    <tb:column name="startStatus" label="${message(code: 'rawMaritalRelationship.startStatus.label')}" />
    <tb:column name="startDate" label="${message(code: 'rawMaritalRelationship.startDate.label')}" />
    <tb:column name="endStatus" label="${message(code: 'rawMaritalRelationship.endStatus.label')}" />
    <tb:column name="endDate" label="${message(code: 'rawMaritalRelationship.endDate.label')}" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross" />
</tb:tabulator>