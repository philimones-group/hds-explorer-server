<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="default.application.name"/></title>

    <asset:stylesheet src="login.css" />
</head>
<body>


    <div class="svg" role="presentation">
        <div class="grails-logo-container">
            <asset:image src="grails-cupsonly-logo-white.svg" class="grails-logo"/>
        </div>
    </div>

    <div id="content" role="main">
        <section class="row colset-2-its">
            <h1><g:message code="default.main.welcome.title" /></h1>
            <br>
            <p><g:message code="default.main.welcome.msg" /></p>
            <br>
            <br>
            <br>
            <div id="controllers" role="navigation">
                <h2><g:message code="default.main.welcome.links" /></h2>
                <ul>
                    <li class="controller"> <g:link url="https://github.com/philimones-group/hds-explorer-server"><g:message code="default.main.welcome.server.source" /></g:link></li>
                    <li class="controller"> <g:link url="https://github.com/philimones-group/hds-explorer-tablet"><g:message code="default.main.welcome.mobile.source" /></g:link></li>
                    <li class="controller"> <g:link controller="syncFiles" action="downloadAndroidApk"><g:message code="default.main.welcome.mobile.apk.download" /></g:link></li>
                </ul>
            </div>

            <br>
            <br>
            <br>
            <p><g:message code="default.main.welcome.developer" /></p>
            <p><g:message code="default.main.welcome.developer_email" /></p>


        </section>
    </div>

</body>
</html>
