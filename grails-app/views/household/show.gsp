<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'household.label', default: 'Household')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>

        <dt:defaultResources/>
    </head>
    <body>
        <a href="#show-household" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
            </ul>
        </div>
        <div id="show-household" class="content scaffold-show" role="main">
            <h1><g:message code="household.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            
            <bi:field bean="${this.household}" property="code"  label="household.code.label" mode="show" />
            <bi:field bean="${this.household}" property="parentRegion" label="household.region.label" mode="show" />
            <bi:field bean="${this.household}" property="name" label="household.name.label" mode="show" />
            <bi:field bean="${this.household}" property="headCode" label="household.headCode.label" mode="show" />
            <bi:field bean="${this.household}" property="headName" label="household.headName.label" mode="show" />
            <bi:field bean="${this.household}" property="createdBy" label="household.createdBy.label" mode="show" />
            <bi:dateField bean="${this.household}" property="createdDate" label="household.createdDate.label" mode="show" />
            <bi:field bean="${this.household}" property="updatedBy" label="household.updatedBy.label" mode="show" />
            <bi:dateField bean="${this.household}" property="updatedDate" label="household.updatedDate.label" mode="show" />

        </div>

        <br>

        <div id="list-member" class="content scaffold-list" role="main">
            <h1><g:message code="household.residents.label" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>


            <dt:table id="memberTable">
                <thead>
                <tr>
                    <g:sortableColumn property="code" title="${message(code: 'member.code.label')}" />
                    <g:sortableColumn property="name" title="${message(code: 'member.name.label')}" />
                    <g:sortableColumn property="gender" title="${message(code: 'member.gender.label')}" />
                    <g:sortableColumn property="dob" title="${message(code: 'member.dob.label')}" />
                    <g:sortableColumn property="householdCode" title="${message(code: 'member.householdCode.label')}" />
                    <g:sortableColumn property="collectedDate" title="${message(code: 'member.collectedDate.label')}" />
                    <g:sortableColumn property="createdDate" title="${message(code: 'member.createdDate.label')}" />
                </tr>
                </thead>
                <tbody>
                <g:each in="${residentsList}" status="i" var="memberInstance">
                    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                        <td><g:link controller="member" action="show" id="${memberInstance.id}">${fieldValue(bean: memberInstance, field: "code")}</g:link></td>
                        <td>${fieldValue(bean: memberInstance, field: "name")}</td>
                        <td><g:message code="${memberInstance?.gender.name}" /></td>
                        <td>${fieldValue(bean: memberInstance, field: "dob")}</td>
                        <td><g:message code="${memberInstance?.householdCode}" /></td>
                        <td><bi:formatDate date="${memberInstance?.collectedDate}" format="yyyy-MM-dd HH:mm:ss" /></td>
                        <td><bi:formatDate date="${memberInstance?.createdDate}" format="yyyy-MM-dd HH:mm:ss" /></td>
                    </tr>
                </g:each>
                </tbody>
            </dt:table>

            <dt:loadDatatable name="memberTable" />
        </div>

        <div id="show-household2" class="content scaffold-show" role="main">
            <g:form resource="${this.household}" >
                <fieldset class="buttons">
                    <!-- <g:link class="edit" action="edit" resource="${this.household}"><g:message code="default.button.edit.label" default="Edit" /></g:link> -->
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>

    </body>
</html>
