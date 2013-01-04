<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
		
		<c:if test="${!editUserPage.hasPassword}">
		showPasswordFields();
		</c:if>
	
		$("#button-delete").click(function() {
			$("#action").val("delete");
		    $(this).closest("form").submit();
		});

		$("#button-cancel").click(function() {
		    window.location = "<c:url value="/app/configuration/administration/list-users" />";
		});

		$("#button-change-password").click(function() {
		    showPasswordFields();
		});

    });
    
    function showPasswordFields() {
		$("#action").val("changePassword");
	    $("#change-password").show();
	    $("#button-change-password").hide();	
    }
    
    
</script>



<form:form commandName="editUserPage">

	<form:hidden path="action" />

	<form:errors path="*" cssClass="error-box" />

	<c:if test="${editUserPage.user.editable}">
		<form:checkbox path="user.enabled" value="true" id="user_enabled" />
		<label for="user_enabled"><spring:message code="configuration.administration.edit-user.enabled" /></label>
	</c:if>


	<label class="new-line"><spring:message code="configuration.administration.edit-user.username" /></label>
	<form:input path="user.username" />
	<br />

	<label class="new-line"><spring:message code="configuration.administration.edit-user.name" /></label>
	<form:input path="user.name" />
	<br />


	<label class="new-line"><spring:message code="configuration.administration.edit-user.roles" /></label>
	<c:choose>
		<c:when test="${editUserPage.user.editable}">
			<form:checkboxes path="user.roles" items="${editUserPage.roles}" itemLabel="name" itemValue="idName" cssClass="checkboxes" delimiter="<br/>" />
		</c:when>
		<c:otherwise>
			<form:checkboxes path="user.roles" items="${editUserPage.user.roles}" itemLabel="name" itemValue="idName" cssClass="checkboxes hide" delimiter="<br/>" />
		</c:otherwise>
	</c:choose>
	<br />


	<label class="new-line"><spring:message code="configuration.administration.edit-user.groups" /></label>
	<form:checkboxes path="user.groups" items="${editUserPage.groups}" itemLabel="name" itemValue="idName" cssClass="checkboxes" delimiter="<br/>" />
	<br />

	<fieldset id="change-password" class="hide">
		<legend>
			<spring:message code="configuration.administration.edit-user.change-password" />
		</legend>
		<label class="new-line"><spring:message code="configuration.administration.edit-user.password" /></label>
		<form:password path="user.password" />
		<br /> <label class="new-line"><spring:message code="configuration.administration.edit-user.repeat-password" /></label>
		<form:password path="repeatPassword" />
	</fieldset>


	<div class="button-panel">
		<input id="button-change-password" class="button" type="button" value="<spring:message code="action.change-password"/>" /> <input id="button-delete" class="button" type="button" value="<spring:message code="action.delete"/>" /> <input class="button" type="submit" value="<spring:message code="action.save"/>" /> <input id="button-cancel" class="button cancel" type="button" value="<spring:message code="action.cancel"/>" />
	</div>


</form:form>

