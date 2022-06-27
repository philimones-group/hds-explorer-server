<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-region" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="region.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-region" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <ol class="property-list region">

                <g:if test="${this.region?.code}">
                    <li class="fieldcontain">
                        <span id="code-label" class="property-label">
                            <g:message code="region.code.label" />
                        </span>
                        <span class="property-value" aria-labelledby="code-label">
                            <g:fieldValue bean="${this.region}" field="code" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.name}">
                    <li class="fieldcontain">
                        <span id="name-label" class="property-label">
                            <g:message code="region.name.label" />
                        </span>
                        <span class="property-value" aria-labelledby="name-label">
                            <g:fieldValue bean="${this.region}" field="name" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.hierarchyLevel}">
                    <li class="fieldcontain">
                        <span id="hierarchyLevel-label" class="property-label">
                            <g:message code="region.hierarchyLevel.label" />
                        </span>
                        <span class="property-value" aria-labelledby="hierarchyLevel-label">
                            ${hierarchyLevel}
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.parent}">
                    <li class="fieldcontain">
                        <span id="password-label" class="property-label">
                            <g:message code="region.parent.label" />
                        </span>
                        <span class="property-value" aria-labelledby="parent-label">
                            ${region.parent?.name}
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.modules}">
                    <li class="fieldcontain">
                        <span id="modules-label" class="property-label">
                            <g:message code="region.modules.label" default="Modules" />
                        </span>
                        <span class="property-value" aria-labelledby="modules-label">
                            <ul>
                                <g:each in="${modules}">
                                    <li class="list-style-type: square;">${it.name}</li>
                                </g:each>
                            </ul>

                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.createdBy}">
                    <li class="fieldcontain">
                        <span id="createdBy-label" class="property-label">
                            <g:message code="region.createdBy.label" default="Created By" />
                        </span>
                        <span class="property-value" aria-labelledby="createdBy-label">
                            <g:fieldValue bean="${this.region}" field="createdBy" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.createdDate}">
                    <li class="fieldcontain">
                        <span id="createdDate-label" class="property-label">
                            <g:message code="region.createdDate.label" default="Created Date" />
                        </span>
                        <span class="property-value" aria-labelledby="createdDate-label">
                            <g:fieldValue bean="${this.region}" field="createdDate" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.updatedBy}">
                    <li class="fieldcontain">
                        <span id="updatedBy-label" class="property-label">
                            <g:message code="region.updatedBy.label" default="Updated By" />
                        </span>
                        <span class="property-value" aria-labelledby="updatedBy-label">
                            <g:fieldValue bean="${this.region}" field="updatedBy" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.region?.updatedDate}">
                    <li class="fieldcontain">
                        <span id="updatedDate-label" class="property-label">
                            <g:message code="region.updatedDate.label" default="Updated Date" />
                        </span>
                        <span class="property-value" aria-labelledby="updatedDate-label">
                            <g:fieldValue bean="${this.region}" field="updatedDate" />
                        </span>
                    </li>
                </g:if>


            </ol>



            <g:form resource="${this.region}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.region}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <!--<input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />-->
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
