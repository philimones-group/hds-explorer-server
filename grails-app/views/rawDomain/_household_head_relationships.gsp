<g:message code="rawDomain.helpers.household.label" /> : <b>${household_code}</b>
<br>
<bi:tableList id="relationshipsTable" class="member" columns="code, name, gender, dob, headRelationshipType, startType, startDate, endType, endDate" />

<dt:loadDatatable name="relationshipsTable" data="${createLink(controller: 'rawDomain', action: 'headRelationshipsList', id: household_code)}" columns="code, name, gender, dob, headRelationshipType, startType, startDate, endType, endDate" />
