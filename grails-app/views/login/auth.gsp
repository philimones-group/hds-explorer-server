<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="default.application.name"/></title>

		<style type="text/css" media="screen">

			#status {
				background-color: #eee;
				border: .2em solid #fff;
				margin: 2em 2em 1em;
				padding: 1em;
				width: 19em;
                min-height: 30em;
				float: left;
				-moz-box-shadow: 0px 0px 1.25em #ccc;
				-webkit-box-shadow: 0px 0px 1.25em #ccc;
				box-shadow: 0px 0px 1.25em #ccc;
				-moz-border-radius: 0.6em;
				-webkit-border-radius: 0.6em;
				border-radius: 0.6em;
                font-size: 0.85em;
			}

			.ie6 #status {
				display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
			}

			#status ul {
				font-size: 0.9em;
				list-style-type: none;
				margin-bottom: 0.6em;
				padding: 0;
			}

			#status li {
				line-height: 1.3;
			}

			#status h1 {
				text-transform: uppercase;
				font-size: 1.1em;
				margin: 0 0 0.3em;
			}

			#page-body {
				margin: 2em 1em 1.25em 18em;
			}

            @media screen and (max-width: 480px) {
                #page-body {
                    margin: 0 1em 1em;
                }

                #page-body h1 {
                    margin-top: 0;
                }
            }

            h1 {
                color: #48802c;
                font-weight: normal;
                font-size: 18px;
                margin: 0.8em 0 0.3em 0;
            }

			h2 {
                color: #48802c;
				margin-top: 1em;
				margin-bottom: 0.3em;
				font-size: 1em;
			}

			p {
				line-height: 1.5;
				margin: 0.25em 0;
			}

			#controller-list ul {
				list-style-position: inside;
			}

			#controller-list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.25em 0;
			}

            .login_message {
                background: #f3f8fc url(../images/skin/information.png) 8px 50% no-repeat;
                border: 1px solid #b2d1ff;
                color: #006dba;
                margin: 10px 0 5px 0;
                padding: 5px 5px 5px 30px;
                font-size: 1em;
            }

            input, select, textarea {
                background-color: #fcfcfc;
                border: 1px solid #cccccc;
                font-size: 14px;
                padding: 0.2em 0.4em;
            }


        </style>
	</head>
	<body>
		<div id="status" role="complementary">
			<h1><g:message code="login.label"/></h1>

            <g:if test='${flash.message}'>
                <div class='login_message'>${flash.message}</div>
            </g:if>

            <form action='${postUrl}' method='POST' id='loginForm' class="cssform" autocomplete='on'>
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
