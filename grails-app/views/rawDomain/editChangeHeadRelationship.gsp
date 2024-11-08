<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'rawChangeHeadRelationship.label', default: 'rawChangeHeadRelationship')}" />
    <title><g:message code="rawChangeHeadRelationship.${mode}.label" args="[entityName]" /></title>

    <style>

        .rerrors {
            font-size: 1em;
            line-height: 2;
            margin: 1em 2em;
            padding: 0.25em;
        }

        .rerrors {
            background: #d6e7d0;
            border: 2px solid #ffaaaa;
            color: #cc0000;
            -moz-box-shadow: 0 0 0.25em #ff8888;
            -webkit-box-shadow: 0 0 0.25em #ff8888;
            box-shadow: 0 0 0.25em #ff8888;
        }

        .rerrors_div {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            margin: 0.8em 1.3em 0.3em;
        }

        .rerrors_title {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            padding: 0 0.3em;
        }

        .rerrors ul {
            padding: 1em;
        }

        .errors li {
            list-style: none;
            background: transparent url(../images/skin/exclamation.png) 0.5em 50% no-repeat;
            text-indent: 2.2em;
            padding: 0 0.25em;
        }

    </style>

</head>
<body>

<a href="#${mode}-rawChangeHeadRelationship" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="${mode}-rawChangeHeadRelationship" class="content scaffold-${mode}" role="main">

    <h1><g:message code="rawChangeHeadRelationship.${mode}.label" default="Edit Raw Change Head Relationship" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
        <br>
    </g:if>


    <g:if test="${errorMessages}">
        <div class="rerrors_div">
            <p class="rerrors_title"><b><g:message code="rawDomain.errors.messages.title.label" /></b></p>
            <ul class="rerrors" role="alert">
                <g:each in="${errorMessages}" status="i" var="error">
                    <li data-field-id="${error.text}">${error.text}</li>
                </g:each>
            </ul>
        </div>
        <br>
    </g:if>

    <g:hasErrors bean="${this.rawChangeHeadRelationship}">
        <ul class="errors" role="alert">
            <g:eachError bean="${this.rawChangeHeadRelationship}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form controller="rawDomain" method="PUT">
        <g:hiddenField name="id" value="${this.rawChangeHeadRelationship?.id}" />
        <g:hiddenField name="version" value="${this.rawChangeHeadRelationship?.version}" />
        <g:hiddenField name="reset" value="${false}" />

        <fieldset class="form">

            <bi:field bean="${this.rawChangeHeadRelationship}" property="id"    label="rawChangeHeadRelationship.id.label" mode="show" />
            <bi:field bean="${this.rawChangeHeadRelationship}" property="changeHead" subProperty="id" label="rawChangeHeadRelationship.changeHead.label" mode="show" />
            <bi:field bean="${this.rawChangeHeadRelationship}" property="newMemberCode"    label="rawChangeHeadRelationship.newMemberCode.label" mode="${mode}" />
            <bi:field bean="${this.rawChangeHeadRelationship}" property="newRelationshipType"    label="rawChangeHeadRelationship.newRelationshipType.label" mode="${mode}" options="HeadRelationshipType" />


        </fieldset>


        <fieldset class="buttons">
            <g:if test="${mode == "edit"}">                
                <g:actionSubmit class="save" value="${message(code: "rawDomain.update.label")}" action="updateChangeHeadRelationship" />
                <g:actionSubmit class="delete" value="${message(code: 'default.button.delete.label')}" action="deleteChangeHeadRelationship" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />

            </g:if>
            <g:else>
                <g:link class="edit" action="editChangeHead" id="${this.rawChangeHeadRelationship.id}" ><g:message code="rawDomain.edit.label" /></g:link>
            </g:else>
        </fieldset>
    </g:form>
</div>
</body>`
</html>
