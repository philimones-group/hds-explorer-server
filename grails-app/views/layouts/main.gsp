<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title><g:message code="default.application.name"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="hds.ico" type="image/x-ico" />

    <asset:stylesheet src="menu.css" />
    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>

    <g:layoutHead/>
</head>
<body>

    <div class="hnavbar">

    <div class="hnavbar-header">
        <div class="hnavbar-header-logo">
            <asset:image src="hds_explorer_logo.png" width="283" height="70" />
        </div>
    </div>

    <div class="hnavbar-header-menu">
        <hds:menuBar>

            <sec:ifLoggedIn>
                <!-- <g:message code="login.as.label"/>: <b><bi:showLoggedUser/></b> (<g:link controller="logout"><g:message code="login.logout.label"/></g:link>) -->

                <hds:menu label="${g.message(code: 'login.logout.label')}"  link="${createLink(controller: 'logout')}"  background="true"/>

            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <hds:menu label="${g.message(code: 'login.button.label')}"  link="${createLink(controller: 'login', action: 'auth')}"  background="true" />
            </sec:ifNotLoggedIn>

            <!--<hds:menu label="${g.message(code: 'default.menu.home.label')}"  link="${createLink(uri: '/')}" />-->
            <hds:menu label="${g.message(code: 'default.menu.dashboard.label')}"  link="${createLink(controller: 'dashboard', action: 'index')}" />
            <hds:menu label="${g.message(code: 'default.menu.users.label')}" link="${createLink(controller: 'user', action: 'index')}" />

            <hds:dropmenu label="${g.message(code: 'default.menu.updates.label')} ">
                <hds:menu label="${g.message(code: 'default.menu.updates.forms.label')}" link="${createLink(controller: 'form', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.updates.rounds.label')}" link="${createLink(controller: 'round', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.updates.regions.label')}" link="${createLink(controller: 'region', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.updates.households.label')}" link="${createLink(controller: 'household', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.updates.members.label')}" link="${createLink(controller: 'member', action: 'index')}" class="arrow"/>
            </hds:dropmenu>
            <hds:dropmenu label="${g.message(code: 'default.menu.lists.label')}">
                <hds:menu label="${g.message(code: 'default.menu.lists.trackinglists.label')}" link="${createLink(controller: 'trackingList', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.updates.datasets.label')}" link="${createLink(controller: 'dataset', action: 'index')}" class="arrow" />
            </hds:dropmenu>
            <hds:dropmenu label="${g.message(code: 'default.menu.sync.label')}">
                <hds:menu label="${g.message(code: 'default.menu.sync.syncdss.label')}" link="${createLink(controller: 'eventSync', action: 'index')}" class="arrow" />
                <!--<hds:menu label="${g.message(code: 'default.menu.sync.import_xls')}" link="#" />-->
                <hds:menu label="${g.message(code: 'default.menu.sync.export.label')}"  link="${createLink(controller: 'syncFiles', action: 'index')}" class="arrow" />
            </hds:dropmenu>
            <hds:dropmenu label="${g.message(code: "default.menu.settings.label")}">
                <hds:menu label="${g.message(code: 'default.menu.settings.modules.label')}" link="${createLink(controller: 'module', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.settings.coreformsext.label')}" link="${createLink(controller: 'coreFormExtension', action: 'index')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.settings.coreformoptions.label')}" link="${createLink(controller: 'settings', action: 'customOptions')}" class="arrow" />
                <hds:menu label="${g.message(code: 'default.menu.settings.hierarchylevels.label')}" link="${createLink(controller: 'applicationParam', action: 'hierarchyLevels')}" class="arrow" />
                <sec:ifAllGranted roles="${org.philimone.hds.explorer.server.model.authentication.Role.ROLE_ADMINISTRATOR}">
                    <hds:menu label="${g.message(code: 'default.menu.settings.system.label')}" link="${createLink(controller: 'settings', action: 'parameters')}" class="arrow" />
                </sec:ifAllGranted>
            </hds:dropmenu>

            <hds:dropmenu label="${g.message(code: 'default.language.select')}">
                <hds:selectLanguageMenu class="arrow" />
            </hds:dropmenu>
        </hds:menuBar>
    </div>

    <div style="clear: both;"></div>

</div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <section id="footer">
        <div id="footerMenu">
            <div id="siteInfo" class="siteInfo" align="right">
                <a href="#">Paulo Filimone </a> | &copy;2022 HDS-Explorer Server <g:meta name="info.app.version"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </div>

        </div>
    </section>

</body>
</html>
