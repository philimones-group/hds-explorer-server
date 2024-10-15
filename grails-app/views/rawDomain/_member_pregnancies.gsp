<g:message code="rawDomain.helpers.member.label" /> : <b>${member_code}</b><br>
<g:message code="rawDomain.helpers.member.name.label" /> : <b>${member_name}</b><br>
<g:message code="rawDomain.helpers.member.gender.label" /> : <b>${member_gender}</b><br>
<g:message code="rawDomain.helpers.member.dob.label" /> : <b>${member_dob}</b><br>
<h2><g:message code="rawDomain.helpers.member.pregnancyregs.label" /></h2>

<tb:tabulator id="memberPregnanciesTable" name="memberPregnanciesTable" paginationSize="5"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMemberPregnanciesList', id: member_code)}">

    <tb:column name="code" label="${message(code: 'rawPregnancyRegistration.code.label')}" />
    <tb:column name="visitCode" label="${message(code: 'rawPregnancyRegistration.visitCode.label')}" />
    <tb:column name="recordedDate" label="${message(code: 'rawPregnancyRegistration.recordedDate.label')}" />
    <tb:column name="expectedDeliveryDate" label="${message(code: 'rawPregnancyRegistration.expectedDeliveryDate.short.label', default: 'EDD')}" />
    <tb:column name="status" label="${message(code: 'rawPregnancyRegistration.status.label')}"  />
</tb:tabulator>

<h2><g:message code="rawDomain.helpers.member.pregnancyouts.label" /></h2>
<tb:tabulator id="memberOutcomesTable" name="memberOutcomesTable" paginationSize="5"
              data="${g.createLink(controller: 'rawDomain', action: 'fetchMemberOutcomesList', id: member_code)}">

    <tb:column name="code" label="${message(code: 'rawPregnancyOutcome.code.label')}" />
    <tb:column name="visitCode" label="${message(code: 'rawPregnancyOutcome.visitCode.label')}" />
    <tb:column name="outcomeDate" label="${message(code: 'rawPregnancyOutcome.outcomeDate.label')}" />
    <tb:column name="numberOfOutcomes" label="${message(code: 'rawPregnancyOutcome.numberOfOutcomes.label', default: 'EDD')}" />
    <tb:column name="childs" label="${message(code: 'rawPregnancyOutcome.childs.label')}"  />
</tb:tabulator>