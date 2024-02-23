<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'coreFormExtension.label', default: 'CoreFormExtension')}" />
        <title><g:message code="coreFormExtension.columns.title.label" /></title>

        <dt:tabulatorResources />
    </head>
    <body>
        <a href="#show-coreFormExtension" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-coreFormExtension" class="content scaffold-show" role="main">
            <h1><g:message code="coreFormExtension.columns.title.label" /></h1>
            <br>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div class="whitebox_panel">

                <ol class="property-list form">

                    <g:if test="${this.coreFormExtension?.formName}">
                        <li class="fieldcontain">
                            <span id="formName-label" class="property-label"><g:message code="coreFormExtension.formName.label"  /></span>
                            <span class="property-value" aria-labelledby="formName-label"><g:message code="${this.coreFormExtension?.formName}" /></span>
                        </li>
                    </g:if>

                    <g:if test="${this.coreFormExtension?.extFormId}">
                        <li class="fieldcontain">
                            <span id="extFormId-label" class="property-label"><g:message code="coreFormExtension.mapping.dbColumnTable" /></span>
                            <span class="property-value" aria-labelledby="extFormId-label"><g:fieldValue bean="${this.coreFormExtension}" field="extFormId" /></span>
                        </li>
                    </g:if>

                    <g:if test="${databaseSystem}">
                        <li class="fieldcontain">
                            <span id="databaseSystem-label" class="property-label"><g:message code="coreFormExtension.columns.database.label" /></span>
                            <span class="property-value" aria-labelledby="databaseSystem-label">${databaseSystem}</span>
                        </li>
                    </g:if>

                    <g:if test="${totalColumns}">
                        <li class="fieldcontain">
                            <span id="totalColumns-label" class="property-label"><g:message code="coreFormExtension.columns.total.label" /></span>
                            <span class="property-value" aria-labelledby="totalColumns-label">${totalColumns}</span>
                        </li>
                    </g:if>
                </ol>
            </div>

            <g:form controller="coreFormExtension" method="POST">
                <g:hiddenField name="id" value="${this.coreFormExtension?.id}" />
                <g:hiddenField name="databaseSystem" value="${databaseSystem}" />
                <g:hiddenField name="totalColumns" value="${totalColumns}" />
                <g:hiddenField name="sqlcommands" value="${sqlCommands}" />

                <div class="whitebox_panel">
                    <label for="exampleFormControlTextarea1"><g:message code="coreFormExtension.columns.sql.execute.title.label" /></label>
                    <g:textArea class="form-control bg-dark text-light" name="sqlCommands" value="${sqlCommands}" rows="10" cols="40"/>
                </div>

                <div class="whitebox_panel">
                    <b><g:message code="coreFormExtension.columns.sql.execute.output.label" /></b>

                    <g:if test="${resultMessages}">

                        <table class="dbreport" >
                            <tbody>
                                <g:each in="${resultMessages}" status="i" var="result">
                                    <tr class="${result[2].equals('true') ? 'dbreport_success' : 'dbreport_error'}">
                                        <td>
                                            <span style="color: #000000;"><g:message code="coreFormExtension.columns.sql.result.msgprefix.label" /> </span><span style="color: #002752;" >${result[0]}</span>
                                        </td>
                                        <g:if test="${result[2].equals('true')}">
                                            <td style="color: #006dba; moz-box-shadow: 0 0 0.25em #b2d1ff;">[${result[1]}]</td>
                                        </g:if>
                                        <g:else>
                                            <td>[${result[1]}]</td>
                                        </g:else>

                                    </tr>

                                </g:each>
                            </tbody>
                        </table>
                        <br>
                    </g:if>

                </div>

                <fieldset class="navbar navbar-dark bg-dark">


                    <div class="float-left">
                        <g:if test="${totalColumns > 0}" >
                            <g:actionSubmit name="btinit" class="btn btn-danger float-left" action="executeAlterTable" value="${g.message(code: 'coreFormExtension.columns.executesql.label')}" />
                        </g:if>
                        &nbsp;
                        <g:actionSubmit name="btsave" class="btn btn-primary" action="dbExtension" value="${g.message(code: 'coreFormExtension.mapping.button.extension.label')}" />
                    </div>
                </fieldset>
            </g:form>
            <br>
        </div>
    </body>
</html>
