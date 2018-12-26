<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="default.application.name"/></title>
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
                <h2>Philimone's Group News:</h2>
                <ul>
                    <li class="controller"> <g:link url="https://bitbucket.com/HDSExplorer">HDS Explorer Public repository (soon)</g:link></li>
                    <li class="controller"> <g:link controller="exportFiles" action="downloadAndroidApk">Download HDS-Explorer for Android (APK)</g:link></li>
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
