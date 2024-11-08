<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'household.label', default: 'Household')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>

        <dt:defaultResources/>
        <tb:tabulatorResources/>
        <tb:luxonResources/>
        <tb:jquiResources/>
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

        <g:set var="household_code" value="${this.household.code}" />
        <g:set var="household_name" value="${this.household.name}" />

        <div id="show-household2" class="content scaffold-show" role="main">
            <g:form resource="${this.household}" >
                <fieldset class="buttons">
                    <!-- <g:link class="edit" action="edit" resource="${this.household}"><g:message code="default.button.edit.label" default="Edit" /></g:link> -->
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

                    <input class="list" type="button" data-toggle="modal" data-target="#show_household_residents" value="${message(code: 'rawDomain.helpers.button.household.residents.label', default: 'Show Residents')}" />

                    <g:render template="show_household_residents"/>

                </fieldset>


            </g:form>
        </div>

    </body>
</html>
