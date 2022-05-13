<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>

        <g:javascript>

            var levelNamesMap = JSON.parse("${hierarchyLevelsJson}");

            $(window).load(
                function () {
                    //alert("testing jquery");

                    $("#div_hierarchy1").hide();
                    $("#div_hierarchy2").hide();
                    $("#div_hierarchy3").hide();
                    $("#div_hierarchy4").hide();
                    $("#div_hierarchy5").hide();
                    $("#div_hierarchy6").hide();
                    $("#div_hierarchy7").hide();
                    $("#div_hierarchy8").hide();
                    $("#div_hierarchy9").hide();
                    $("#divParent").hide();

                    //if is already selected
                    var level = $("#hierarchyLevel").val();
                    var numberstr = level.replace("hierarchy", "");
                    var levelNumber = parseInt(numberstr);

                    if (levelNumber==1){
                        hideAll();
                        unselectAll();
                    } else if (levelNumber > 1) { //2-onwards
                        //$("#divParent").show();
                        showParent(levelNumber-1);

                        hideFrom(levelNumber-1); //from number-1 to 9
                        showFrom(levelNumber-2); //from number-2 to 1

                        //if is 2         -> load hierarchy1 to divParent
                        if (levelNumber == 2) {
                            //load hierarch1 regions to #parent
                            loadRegions("hierarchy1", "parent");
                        } else if (levelNumber > 2){
                            loadRegions("hierarchy1", "hierarchy1");
                        }

                    }

               }
            );

            $(document).ready(function() {

                $("#name").change(function() {
                    $.ajax({
                        url: "${createLink(controller: "region", action: "generateCodeGsp")}",
                        data: "name=" + this.value,
                        cache: false,
                        success: function(html) {
                            $("#code").val(html);

                        }
                    });
                });

                $("#hierarchyLevel").change(function() {
                    var level = "" + this.value;
                    var numberstr = level.replace("hierarchy", "");
                    var levelNumber = parseInt(numberstr);

                    if (levelNumber==1){
                        hideAll();
                        unselectAll();
                    } else if (levelNumber > 1) { //2-onwards
                        //$("#divParent").show();
                        showParent(levelNumber-1);

                        hideFrom(levelNumber-1); //from number-1 to 9
                        showFrom(levelNumber-2); //from number-2 to 1

                        //if is 2         -> load hierarchy1 to divParent
                        if (levelNumber == 2) {
                            //load hierarch1 regions to #parent
                            loadRegions("hierarchy1", "parent");
                        } else if (levelNumber > 2){
                            loadRegions("hierarchy1", "hierarchy1");
                        }

                    }
                });

                $("#hierarchy1").change(function() {
                    var currentHierarchy = 1;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy2").change(function() {
                    var currentHierarchy = 2;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy3").change(function() {
                    var currentHierarchy = 3;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy4").change(function() {
                    var currentHierarchy = 4;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy5").change(function() {
                    var currentHierarchy = 5;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy6").change(function() {
                    var currentHierarchy = 6;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy7").change(function() {
                    var currentHierarchy = 7;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy8").change(function() {
                    var currentHierarchy = 8;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

                $("#hierarchy9").change(function() {
                    var currentHierarchy = 9;
                    var hierarchyLevel = $('#hierarchyLevel option:selected').val()
                    var regionId = this.value;
                    var strlevel = hierarchyLevel.replace("hierarchy", "");
                    var level = parseInt(strlevel);

                    //if level = 2  -> we do nothing because we can select h1 only
                    if (level-currentHierarchy == 2) { //hierarchy3 -> load values to parent=hierarchy2
                        loadRegionsById(regionId, "parent");
                    } else if (level-currentHierarchy > 2){                         //l-c
                        var next = currentHierarchy+1

                        //load hierarchyNext
                        loadRegionsById(regionId, "hierarchy"+next);
                    }
                });

            });

            function loadRegions(hierarchyLevel, selectId) {
                $.ajax({
                    url: "${createLink(controller: "region", action: "loadRegionsToGsp")}",
                    data: "name="+hierarchyLevel+":"+selectId,
                    cache: false,
                    success: function(html) {
                        $("#"+selectId).html(html);
                    }
                });
            }

            function loadRegionsById(parentRegionId, selectId) {
                $.ajax({
                    url: "${createLink(controller: "region", action: "loadRegionsByIdToGsp")}",
                    data: "name="+parentRegionId+":"+selectId,
                    cache: false,
                    success: function(html) {
                        $("#"+selectId).html(html);
                        $("#"+selectId).val('');
                    }
                });
            }

            function hideAll() {

                for (var i=1; i <= 9; i++) {
                    $("#div_hierarchy"+i).hide();
                    $("#hierarchy"+i).val('');
                    $("#hierarchy"+i).empty();
                }

                $("#divParent").hide();
                $("#parent").empty();

            }

            function hideFrom(number) {
                for (var i=number; i <= 9; i++) {
                    $("#div_hierarchy"+i).hide();
                    $("#hierarchy"+i).val('');
                    $("#hierarchy"+i).empty();
                }
            }

            function showFrom(number) {
                for (var i=number; i >= 1; i--) {
                    var level = "hierarchy"+i;
                    var labelText = getParentLabel(i);

                    $("#div_"+level).show();
                    $("#label_"+level).text(labelText+" **");
                    $("#"+level).val('');
                    $("#"+level).empty();
                }
            }

            function showParent(hierarchyNumber) {

                var labelText = getParentRegionLabel(hierarchyNumber);

                $("#divParent").show();
                $("#label_parent").text(labelText+" **");
                $("#parent").val('');
                $("#parent").empty();
            }

            function getParentLabel(hierarchyNumber) {
                var level = "hierarchy"+hierarchyNumber;
                var name = levelNamesMap[level];
                var message = "${message(code: 'region.parent.cascade.label')}";
                message = message.replace("{1}", name);

                return message;
            }

            function getParentRegionLabel(hierarchyNumber) {
                var level = "hierarchy"+hierarchyNumber;
                var name = levelNamesMap[level];
                var message = "${message(code: 'region.parent.region.label')}";
                message = message.replace("{1}", name);

                return message;
            }

            function unselectAll(){
                $("#hierarchy1").val('');
                $("#hierarchy2").val('');
                $("#hierarchy3").val('');
                $("#hierarchy4").val('');
                $("#hierarchy5").val('');
                $("#hierarchy6").val('');
                $("#hierarchy7").val('');
                $("#hierarchy8").val('');
                $("#hierarchy9").val('');
                $("#parent").val('');
            }

        </g:javascript>

        <a href="#create-region" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-region" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>


            <g:if test="${errorMessages}">
                <ul class="errors" role="alert">
                    <g:each in="${errorMessages}" status="i" var="error">
                        <li data-field-id="${error.text}">${error.text}</li>
                    </g:each>
                </ul>
            </g:if>

            <g:form resource="${this.region}" method="POST">
                <fieldset class="form">

                    <div class="fieldcontain ${hasErrors(bean: this.region, field: 'code', 'error')} required">
                        <label for="code">
                            <g:message code="region.code.label" default="Region Code" /><span class="required-indicator">*</span>
                        </label>
                        <g:textField name="code" value="${this.region.code}" readonly="readonly"/>
                    </div>

                    <f:field bean="region" property="name" />

                    <div class="fieldcontain ${hasErrors(bean: this.region, field: 'hierarchyLevel', 'error')} required">
                        <label for="hierarchyLevel">
                            <g:message code="region.hierarchyLevel.label" default="Hierarchy Level" /><span class="required-indicator">*</span>
                        </label>

                        <g:select name="hierarchyLevel" optionKey="level" optionValue="name" from="${regionLevels}" value="${hierarchyLevel}" />
                    </div>

                    <div id="div_hierarchy1">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy1" for="hierarchy1">Parent region 1</label>
                            <g:select name="hierarchy1" optionKey="id" optionValue="name" from="" noSelection="['':'']"/>
                        </div>
                    </div>

                    <div id="div_hierarchy2">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy2" for="hierarchy2">Parent region 2</label>
                            <g:select name="hierarchy2" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy3">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy3" for="hierarchy3">Parent region 3</label>
                            <g:select name="hierarchy3" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy4">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy4" for="hierarchy4">Parent region 4</label>
                            <g:select name="hierarchy4" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy5">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy5" for="hierarchy5">Parent region 5</label>
                            <g:select name="hierarchy5" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy6">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy6" for="hierarchy6">Parent region 6</label>
                            <g:select name="hierarchy6" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy7">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy7" for="hierarchy7">Parent region 7</label>
                            <g:select name="hierarchy7" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy8">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy8" for="hierarchy8">Parent region 8</label>
                            <g:select name="hierarchy8" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="div_hierarchy9">
                        <div class="fieldcontain required">
                            <label id="label_hierarchy9" for="hierarchy9">Parent region 9</label>
                            <g:select name="hierarchy9" optionKey="id" optionValue="name" from="" noSelection="['':'']" />
                        </div>
                    </div>

                    <div id="divParent">
                        <div class="fieldcontain ${hasErrors(bean: this.region, field: 'parent', 'error')} required">
                            <label id="label_parent" for="parent">
                                <g:message code="region.parent.label" args="" default="Parent region" /><span class="required-indicator">*</span>
                            </label>

                            <g:select name="parent" optionKey="id" optionValue="name" from="" value="${this.region?.parent}" noSelection="['':'']"/>
                        </div>
                    </div>
                    
                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
