<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-region" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="region.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-region" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>


            <div class="whitebox_panel">
                <bi:ifRegionHeadSupported>
                    <bi:tableList id="regionTable" class="region" columns="code, name, hierarchyLevel, hierarchyName, head, parent, createdBy, createdDate" />
                </bi:ifRegionHeadSupported>
                <bi:ifRegionHeadNotSupported>
                    <bi:tableList id="regionTable" class="region" columns="code, name, hierarchyLevel, hierarchyName, parent, createdBy, createdDate" />
                </bi:ifRegionHeadNotSupported>
            </div>

            <bi:ifRegionHeadSupported>
                <dt:loadDatatable name="regionTable" data="${createLink(controller: 'region', action: 'regionList')}" columns="code, name, hierarchyLevel, hierarchyName, head, parent, createdBy, createdDate" />
            </bi:ifRegionHeadSupported>
            <bi:ifRegionHeadNotSupported>
                <dt:loadDatatable name="regionTable" data="${createLink(controller: 'region', action: 'regionList')}" columns="code, name, hierarchyLevel, hierarchyName, parent, createdBy, createdDate" />
            </bi:ifRegionHeadNotSupported>

        </div>

    
    </body>
</html>