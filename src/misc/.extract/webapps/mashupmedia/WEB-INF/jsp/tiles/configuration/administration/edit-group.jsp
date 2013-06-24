<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {

	$("#button-delete").click(function() {
	    $("#action").val("delete");
	    $(this).closest("form").submit();
	});

    });
</script>


<c:url var="actionUrl" value="/app/configuration/administration/submit-group" />
<form:form commandName="editGroupPage" action="${actionUrl}">

	<form:hidden path="action" />
	<form:hidden path="group.id" />

	<form:errors path="*" cssClass="error-box" />

	<label class="new-line"><spring:message code="configuration.administration.edit-group.name" /></label>
	<form:input path="group.name" />
	<br />


	<c:if test="${fn:length(libraries) > 0}">
		<label class="new-line"><spring:message code="configuration.administration.edit-group.libraries" /></label>
		<form:checkboxes path="selectedLibraries" items="${libraries}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
		<br />
	</c:if>

	<div class="button-panel">
		<c:if test="${editUserPage.group.id > 0}">
			<input id="button-delete" class="button" type="button" value="<spring:message code="action.delete"/>" />
		</c:if>
		<input class="button" type="submit" value="<spring:message code="action.save"/>" /> <input id="button-cancel" class="button cancel" type="button" value="<spring:message code="action.cancel"/>" />
	</div>


</form:form>

