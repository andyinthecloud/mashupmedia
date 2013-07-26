<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<script type="text/javascript">
    $(document).ready(function() {
    	$("form input").attr('autocomplete','off');
		
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
		
		<c:if test="${!empty hasErrors}">
	    	showPasswordFields();		
		</c:if>

    });
    
    function showPasswordFields() {
		$("#action").val("changePassword");
	    $("#change-password").show();
	    $("#button-change-password").hide();	
    }
    
    
</script>


<c:url var="actionUrl" value="/app/configuration/administration/submit-user" />
<form:form commandName="editUserPage" action="${actionUrl}" autocomplete="false">

	<form:hidden path="action" />
	<form:hidden path="user.id" />
	<form:errors path="*" cssClass="error-box" />

	<c:choose>
		<c:when test="${editUserPage.user.editable && editUserPage.isAdministrator}">
			<form:checkbox path="user.enabled" value="true" id="user_enabled" />
			<label for="user_enabled"><spring:message code="configuration.administration.edit-user.enabled" /></label>

			<form:checkbox path="administrator" value="true" />
			<label for="administrator1"><spring:message code="configuration.administration.edit-user.administrator" /></label>
		</c:when>

		<c:otherwise>
			<form:hidden path="user.enabled" />
			<form:hidden path="administrator" />
		</c:otherwise>
	</c:choose>

	<label class="new-line"><spring:message code="configuration.administration.edit-user.username" /></label>
	<form:input path="user.username" autocomplete="false"/>
	<br />

	<label class="new-line"><spring:message code="configuration.administration.edit-user.name" /></label>
	<form:input path="user.name" autocomplete="false"/>
	<br />


	<label class="new-line"><spring:message code="configuration.administration.edit-user.groups" /></label>
	<form:checkboxes path="user.groups" items="${groups}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
	<br />

	<fieldset id="change-password" class="hide">
		<legend>
			<spring:message code="configuration.administration.edit-user.change-password" />
		</legend>
		<label class="new-line"><spring:message code="configuration.administration.edit-user.password" /></label>
		<form:password path="user.password" autocomplete="false"/>
		<br /> <label class="new-line"><spring:message code="configuration.administration.edit-user.repeat-password" /></label>
		<form:password path="repeatPassword" autocomplete="false"/>
	</fieldset>



	<div class="button-panel">
		<input id="button-change-password" class="button" type="button" value="<spring:message code="action.change-password"/>" />
		<c:if test="${editUserPage.user.id > 0}">
			<input id="button-delete" class="button" type="button" value="<spring:message code="action.delete"/>" />
		</c:if>
		<input class="button" type="submit" value="<spring:message code="action.save"/>" /> <input id="button-cancel" class="button cancel" type="button" value="<spring:message code="action.cancel"/>" />
	</div>


</form:form>

