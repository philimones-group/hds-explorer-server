<tb:toast id="toast_hhr_msg" title="Info" message="Test message" position="top_center" />

<g:message code="rawDomain.helpers.region.label" /> : <b>${region_code}</b><br>
<g:if test="${old_head_code}" >
    <g:message code="rawDomain.helpers.member.oldheadcode.label" /> : <b>${old_head_code}</b><br>
</g:if>
<g:if test="${old_head_code}">
    <g:message code="rawDomain.helpers.member.newheadcode.label" /> : <b>${new_head_code}</b><br>
</g:if>


<h2><g:message code="rawDomain.helpers.region.headrelationships.all.label" /></h2>
<tb:tabulator id="regionHeadsTable" name="regionHeadsTable" toastid="toast_hhr_msg" contextMenu="true" paginationSize="5"
              data="${createLink(controller: 'rawDomain', action: 'fetchRegionHeadRelationshipsList', id: region_code)}" boxed="false"
              update="${createLink(controller: 'rawDomain', action: 'updateRegionHeadRelationshipField')}">

    <tb:column name="code" label="${message(code: 'region.code.label')}" />
    <tb:column name="name" label="${message(code: 'region.name.label')}" />
    <tb:column name="head"  label="${message(code: 'member.head.label')}" />
    <tb:column name="headName" label="${message(code: 'member.head.name.label')}" />
    <tb:column name="startType"     label="${message(code: 'member.startType.label')}" />
    <tb:column name="startDate" label="${message(code: 'member.startDate.label')}" />
    <tb:column name="endType" label="${message(code: 'member.endType.label')}" />
    <tb:column name="endDate" label="${message(code: 'member.endDate.label')}" />
    <tb:column name="statusText" label="${message(code: 'member.status.label')}" />
    <tb:column name="status" label="" formatter="tickCross"/>
</tb:tabulator>