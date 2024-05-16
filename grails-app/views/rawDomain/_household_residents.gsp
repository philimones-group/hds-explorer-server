<g:message code="rawDomain.helpers.household.label" /> : <b>${household_code}</b>
<br>
<bi:tableList id="residenciesTable" class="member" columns="code, name, gender, dob, startType, startDate, endType, endDate" />

<dt:loadDatatable name="residenciesTable" data="${createLink(controller: 'rawDomain', action: 'residenciesList', id: household_code)}" columns="code, name, gender, dob, startType, startDate, endType, endDate" />