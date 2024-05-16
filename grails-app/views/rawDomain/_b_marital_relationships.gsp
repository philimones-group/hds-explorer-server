<g:message code="rawDomain.helpers.member.b.label" /> : <b>${member_b_code}</b>
<br>
<bi:tableList id="maritalRelationshipsTable" class="rawMaritalRelationship" columns="memberA, memberB, isPolygamic, startStatus, startDate, endStatus, endDate" />

<dt:loadDatatable name="maritalRelationshipsTable" data="${createLink(controller: 'rawDomain', action: 'maritalRelationshipsList', id: member_b_code)}" columns="memberA_code, memberB_code, isPolygamic, startStatus, startDate, endStatus, endDate" />
