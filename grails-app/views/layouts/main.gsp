<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title><g:message code="default.application.name"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <asset:stylesheet src="application.css"/>
    <asset:javascript src="application.js"/>

    <style type="text/css" media="screen">
        #headerLeft{
            float: left;
            margin-left: 0px;
            margin-right: 10px;
        }

    </style>

    <g:layoutHead/>
</head>
<body>

    <div class="navbar navbar-default navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <div id="headerLeft">
                    <asset:image src="hds_explorer_logo.png" width="283" height="70" />
                </div>
            </div>

            <bi:menuBar>

                <sec:ifLoggedIn>
                    <!-- <g:message code="login.as.label"/>: <b><bi:showLoggedUser/></b> (<g:link controller="logout"><g:message code="login.logout.label"/></g:link>) -->

                    <bi:menu label="${g.message(code: 'login.logout.label')}"  link="${createLink(controller: 'logout')}"  style="active"/>

                </sec:ifLoggedIn>
                <sec:ifNotLoggedIn>
                    <bi:menu label="${g.message(code: 'login.button.label')}"  link="${createLink(controller: 'login', action: 'auth')}"  style="active" />
                </sec:ifNotLoggedIn>

                <bi:menu label="${g.message(code: 'default.menu.home.label')}"  link="${createLink(uri: '/')}" />
                <bi:menu label="${g.message(code: 'default.menu.users.label')}" link="${createLink(controller: 'user', action: 'index')}" />

                <bi:dropmenu label="${g.message(code: 'default.menu.updates.label')} ">
                    <bi:menu label="${g.message(code: 'default.menu.updates.forms.label')}" link="${createLink(controller: 'form', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.rounds.label')}" link="${createLink(controller: 'round', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.regions.label')}" link="${createLink(controller: 'region', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.households.label')}" link="${createLink(controller: 'household', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.members.label')}" link="${createLink(controller: 'member', action: 'index')}"/>
                </bi:dropmenu>
                <bi:dropmenu label="${g.message(code: 'default.menu.lists.label')}">
                    <bi:menu label="${g.message(code: 'default.menu.lists.trackinglists.label')}" link="${createLink(controller: 'trackingList', action: 'add')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.datasets.label')}" link="${createLink(controller: 'dataset', action: 'add')}" />
                </bi:dropmenu>
                <bi:dropmenu label="${g.message(code: 'default.menu.sync.label')}">
                    <bi:menu label="${g.message(code: 'default.menu.sync.syncdss.label')}" link="${createLink(controller: 'eventSync', action: 'index')}" />
                    <!--<bi:menu label="${g.message(code: 'default.menu.sync.import_xls')}" link="#" />-->
                    <bi:menu label="${g.message(code: 'default.menu.sync.export.label')}"  link="${createLink(controller: 'syncFiles', action: 'index')}" />
                </bi:dropmenu>
                <bi:dropmenu label="${g.message(code: "default.menu.settings.label")}">
                    <bi:menu label="${g.message(code: 'default.menu.settings.modules.label')}" link="${createLink(controller: 'module', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.settings.coreformsext.label')}" link="${createLink(controller: 'coreFormExtension', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.settings.hierarchylevels.label')}" link="${createLink(controller: 'applicationParam', action: 'hierarchyLevels')}" />
                    <sec:ifAllGranted roles="${org.philimone.hds.explorer.server.model.authentication.Role.ROLE_ADMINISTRATOR}">
                        <bi:menu label="${g.message(code: 'default.menu.settings.system.label')}" link="${createLink(controller: 'settings', action: 'parameters')}" />
                    </sec:ifAllGranted>
                </bi:dropmenu>

                <bi:dropmenu label="${g.message(code: 'default.language.select')}">
                    <language:selectMenu />
                </bi:dropmenu>
            </bi:menuBar>
        </div>
    </div>

    <g:layoutBody/>


    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
        <g:message code="spinner.alt" default="Loading&hellip;"/>
    </div>

    <section id="footer">
        <div id="footerMenu">
            <div id="siteInfo" class="siteInfo" align="right">
                <a href="#">Paulo Filimone </a> | &copy;2022 HDS-Explorer Server <g:message code="default.application.version" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </div>

        </div>
    </section>

</body>
</html>
