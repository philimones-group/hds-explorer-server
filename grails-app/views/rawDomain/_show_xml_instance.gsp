<!-- Modal -->


<div id="show_xml_instance" class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm modal-dialog-centered" style="min-width:80%" >
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title" id="staticBackdropLabel">
                    <g:message code="rawDomain.helpers.show.xml.instance.title.label" />
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <pre id="xmlContent" style="white-space: pre-wrap; word-break: break-word;"><b>${xmlInstance}</b></pre>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"><g:message code="rawDomain.helpers.close.label" /></button>
            </div>

        </div>
    </div>
</div>