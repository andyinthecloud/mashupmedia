<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(
                    function() {

                        $("div.ui-content form").on("click", "#button-delete", function() {
                            $("#action").val("delete");
                            $(this).closest("form").submit();
                        });

                        $("#editGroupPage").submit(
                                        function(event) {
                                            event.preventDefault();
                                            submitAjaxForm($(this), "<spring:message code="configuration.administration.groups.title" />",
                                                            "<c:url value="/app/configuration/administration/list-groups" />");
                                        });

                    });
</script>


<c:url var="actionUrl"
	value="/app/configuration/administration/submit-group" />
<form:form commandName="editGroupPage" action="${actionUrl}">

	<form:hidden path="action" />
	<form:hidden path="group.id" />

	<form:errors path="*" cssClass="error-box" />


	<div class="new-line">
		<label><spring:message
				code="configuration.administration.edit-group.name" /></label>
		<form:input path="group.name" data-clear-btn="true" />
	</div>

	<c:if test="${fn:length(libraries) > 0}">
		<div class="new-line">
			<fieldset data-role="controlgroup">
				<legend>
					<spring:message
						code="configuration.administration.edit-group.libraries" />
				</legend>
				<form:checkboxes path="selectedLibraries" items="${libraries}"
					itemLabel="name" itemValue="id" />
			</fieldset>
		</div>
	</c:if>

	<div>
		<c:if test="${editGroupPage.group.id > 0}">
			<input id="button-delete" class="button" type="button"
				value="<spring:message code="action.delete"/>" />
		</c:if>
		<input class="button" type="submit"
			value="<spring:message code="action.save"/>" />
	</div>

</form:form>

