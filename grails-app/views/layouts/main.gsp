<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title><g:message code="default.application.name"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />

    <asset:stylesheet src="application.css"/>

    <style type="text/css" media="screen">
        #headerLeft{
            float: left;
            height: 40px;
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
                    <asset:image src="hds_explorer_logo.png" />
                </div>

                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>

            </div>
            <bi:menuBar>
                <bi:menu label="${g.message(code: 'default.menu.home.label')}"  link="${createLinkTo(dir: '')}" />
                <bi:menu label="${g.message(code: 'default.menu.users.label')}" link="${createLink(controller: 'user', action: 'index')}" />

                <bi:dropmenu label="${g.message(code: 'default.menu.updates.label')} ">
                    <bi:menu label="${g.message(code: 'default.menu.updates.modules.label')}" link="${createLink(controller: 'studyModule', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.forms.label')}" link="${createLink(controller: 'form', action: 'index')}" />
                    <bi:menuseparator />
                    <bi:menu label="${g.message(code: 'default.menu.updates.households.label')}" />
                    <bi:menu label="${g.message(code: 'default.menu.updates.members.label')}" />
                </bi:dropmenu>
                <bi:dropmenu label="${g.message(code: 'default.menu.lists.label')}">
                    <bi:menu label="${g.message(code: 'default.menu.lists.trackinglists.label')}" />
                </bi:dropmenu>
                <bi:dropmenu label="${g.message(code: 'default.menu.sync.label')}">
                    <bi:menu label="${g.message(code: 'default.menu.sync.import_openhds.label')}" link="${createLink(controller: 'importOpenHDS', action: 'index')}" />
                    <bi:menu label="${g.message(code: 'default.menu.sync.import_xls')}" link="#" />
                    <bi:menu label="${g.message(code: 'default.menu.sync.export.label')}"  link="${createLink(controller: 'exportFiles', action: 'index')}" />
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
                <a href="#">Paulo Filimone </a> | &copy;2018 HDS-Explorer <g:message code="default.application.version" />
            </div>

        </div>
    </section>

    <asset:javascript src="application.js"/>

</body>
</html>
