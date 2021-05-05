<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-user" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>


            <ol class="property-list user">

                <g:if test="${this.user?.firstName}">
                    <li class="fieldcontain">
                        <span id="firstName-label" class="property-label">
                            <g:message code="user.firstName.label" default="First Name" />
                        </span>
                        <span class="property-value" aria-labelledby="firstName-label">
                            <g:fieldValue bean="${this.user}" field="firstName" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.lastName}">
                    <li class="fieldcontain">
                        <span id="lastName-label" class="property-label">
                            <g:message code="user.lastName.label" default="Last Name" />
                        </span>
                        <span class="property-value" aria-labelledby="lastName-label">
                            <g:fieldValue bean="${this.user}" field="lastName" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.username}">
                    <li class="fieldcontain">
                        <span id="username-label" class="property-label">
                            <g:message code="user.username.label" default="Username" />
                        </span>
                        <span class="property-value" aria-labelledby="username-label">
                            <g:fieldValue bean="${this.user}" field="username" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.password}">
                    <li class="fieldcontain">
                        <span id="password-label" class="property-label">
                            <g:message code="user.password.label" default="Password" />
                        </span>
                        <span class="property-value" aria-labelledby="password-label">
                            *************
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.email}">
                    <li class="fieldcontain">
                        <span id="email-label" class="property-label">
                            <g:message code="user.email.label" default="Email" />
                        </span>
                        <span class="property-value" aria-labelledby="email-label">
                            <g:fieldValue bean="${this.user}" field="email" />
                        </span>
                    </li>
                </g:if>

                    <li class="fieldcontain">
                        <span id="passwordExpired-label" class="property-label">
                            <g:message code="user.passwordExpired.label" default="Password Expired" />
                        </span>
                        <span class="property-value" aria-labelledby="passwordExpired-label">
                            <g:fieldValue bean="${this.user}" field="passwordExpired" />
                        </span>
                    </li>


                    <li class="fieldcontain">
                        <span id="accountExpired-label" class="property-label">
                            <g:message code="user.accountExpired.label" default="Account Expired" />
                        </span>
                        <span class="property-value" aria-labelledby="accountExpired-label">
                            <g:fieldValue bean="${this.user}" field="accountExpired" />
                        </span>
                    </li>

                    <li class="fieldcontain">
                        <span id="accountLocked-label" class="property-label">
                            <g:message code="user.accountLocked.label" default="Account Locked" />
                        </span>
                        <span class="property-value" aria-labelledby="accountLocked-label">
                            <g:fieldValue bean="${this.user}" field="accountLocked" />
                        </span>
                    </li>

                    <li class="fieldcontain">
                        <span id="enabled-label" class="property-label">
                            <g:message code="user.enabled.label" default="Enabled" />
                        </span>
                        <span class="property-value" aria-labelledby="enabled-label">
                            <g:fieldValue bean="${this.user}" field="enabled" />
                        </span>
                    </li>

                <g:if test="${this.user?.modules}">
                    <li class="fieldcontain">
                        <span id="modules-label" class="property-label">
                            <g:message code="user.modules.label" default="Modules" />
                        </span>
                        <span class="property-value" aria-labelledby="modules-label">
                            <ul>
                                <g:each in="${this.user.modules}">
                                    <li class="list-style-type: square;">${ message(code: it.name) }</li>
                                </g:each>
                            </ul>

                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.authorities}">
                    <li class="fieldcontain">
                        <span id="authorities-label" class="property-label">
                            <g:message code="user.authorities.label" />
                        </span>
                        <span class="property-value" aria-labelledby="authorities-label">
                            <ul>
                            <g:each in="${this.user.authorities}">
                                <li class="list-style-type: square;">${ message(code: it.name) }</li>
                            </g:each>
                            </ul>
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.createdBy}">
                    <li class="fieldcontain">
                        <span id="createdBy-label" class="property-label">
                            <g:message code="user.createdBy.label" default="Created By" />
                        </span>
                        <span class="property-value" aria-labelledby="createdBy-label">
                            <g:fieldValue bean="${this.user}" field="createdBy" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.createdDate}">
                    <li class="fieldcontain">
                        <span id="createdDate-label" class="property-label">
                            <g:message code="user.createdDate.label" default="Created Date" />
                        </span>
                        <span class="property-value" aria-labelledby="createdDate-label">
                            <g:fieldValue bean="${this.user}" field="createdDate" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.updatedBy}">
                    <li class="fieldcontain">
                        <span id="updatedBy-label" class="property-label">
                            <g:message code="user.updatedBy.label" default="Updated By" />
                        </span>
                        <span class="property-value" aria-labelledby="updatedBy-label">
                            <g:fieldValue bean="${this.user}" field="updatedBy" />
                        </span>
                    </li>
                </g:if>

                <g:if test="${this.user?.updatedDate}">
                    <li class="fieldcontain">
                        <span id="updatedDate-label" class="property-label">
                            <g:message code="user.updatedDate.label" default="Updated Date" />
                        </span>
                        <span class="property-value" aria-labelledby="updatedDate-label">
                            <g:fieldValue bean="${this.user}" field="updatedDate" />
                        </span>
                    </li>
                </g:if>


            </ol>

            <g:form resource="${this.user}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.user}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <g:link class="edit" action="changePassword" resource="${this.user}"><g:message code="user.changePassword.label" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
