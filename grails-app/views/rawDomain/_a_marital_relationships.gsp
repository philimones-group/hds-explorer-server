<g:message code="rawDomain.helpers.member.a.label" /> : <b>${member_a_code}</b>
<br>
<bi:tableList id="relationshipsTable" class="rawMaritalRelationship" columns="memberA, memberB, isPolygamic, startStatus, startDate, endStatus, endDate" />

<dt:loadDatatable name="relationshipsTable" data="${createLink(controller: 'rawDomain', action: 'maritalRelationshipsList', id: member_a_code)}" columns="memberA_code, memberB_code, isPolygamic, startStatus, startDate, endStatus, endDate" />
