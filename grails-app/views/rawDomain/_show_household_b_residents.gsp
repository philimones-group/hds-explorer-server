<!-- Modal -->
<div id="show_household_b_residents" class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" style="min-width:80%" >
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title" id="staticBackdropLabel">

                    <g:if test="${household_residents_title}" >
                        ${household_residents_title}
                    </g:if>
                    <g:else>
                        <g:message code="rawDomain.helpers.household.residents.title.label" />
                    </g:else>
                </h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <g:render template="household_b_residents"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"><g:message code="rawDomain.helpers.close.label" /></button>
            </div>

        </div>
    </div>
</div>