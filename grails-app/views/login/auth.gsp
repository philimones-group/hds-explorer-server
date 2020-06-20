<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="default.application.name"/></title>

		<asset:stylesheet src="login.css" />

	</head>
	<body>
		<div id="status" role="complementary">
			<h1><g:message code="login.label"/></h1>

            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>

            <form action='${postUrl}' method='POST' id='loginForm' autocomplete='on'>
                <p><label for='username'><g:message code="login.username.label"/></label></p>

                <p><input type='text' class='inputtext' name='username' id='username' /></p>

                <p><label for='password'><g:message code="login.password.label"/></label></p>

                <p><input type='password' class='inputtext' name='password' id='password' /></p>

                <p><input type='submit' id="submit" value='${message(code: "login.button.label")}'/></p>
            </form>

            <ul>
                <li><g:message code="login.tips.label"/>
                    <a href="#"><g:message code="login.forgot.password.label"/></a>
                </li>
                <li><g:message code="login.system.version.label"/> <g:meta name="app.version"/></li>
            </ul>

		</div>

        <section class="main_message">
            <h1><g:message code="default.main.welcome.title" /></h1>
            <br>
            <p><g:message code="default.main.welcome.msg" /></p>
            <br>
            <br>
            <br>
            <div id="controllers" role="navigation">
                <h2>Philimone's Group News:</h2>
                <ul>
                    <li class="controller">
                        <g:link url="https://bitbucket.com/HDSExplorer">HDS Explorer Public repository (soon)</g:link>
                    </li>
                </ul>
            </div>

            <br>
            <br>
            <br>
            <p><g:message code="default.main.welcome.developer" /></p>
            <p><g:message code="default.main.welcome.developer_email" /></p>


        </section>
	</body>
</html>
