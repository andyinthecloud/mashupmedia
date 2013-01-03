<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<script type="text/javascript">
    $(document).ready(function() {
	$("#button-change-password").click(function() {
	    $("#change-password").show();
	    $(this).hide();
	});
    });
</script>

<form:form commandName="editUserPage">

	<form:errors path="*" cssClass="error-box" />

	<label class="new-line"><spring:message code="configuration.administration.edit-user.username" /></label>
	<form:input path="user.username" />
	<br />

	<label class="new-line"><spring:message code="configuration.administration.edit-user.name" /></label>
	<form:input path="user.name" />
	<br />

	<label class="new-line"><spring:message code="configuration.administration.edit-user.roles" /></label>
	<form:checkboxes path="user.name" items="${editUserPage.roles}" itemLabel="name" itemValue="idName" cssClass="checkboxes" delimiter="<br/>" />
	<br />

	<fieldset id="change-password" class="hide">
		<legend>
			<spring:message code="configuration.administration.edit-user.change-password" />
		</legend>
		<label class="new-line"><spring:message code="configuration.administration.edit-user.password" /></label>
		<form:password path="user.password" />
		<br /> <label class="new-line"><spring:message code="configuration.administration.edit-user.repeat-password" /></label> <input type="password" />
	</fieldset>



	<div class="button-panel">
		<input id="button-change-password" class="button" type="button" value="<spring:message code="action.change-password"/>" /> <input class="button" type="submit" value="<spring:message code="action.save"/>" /> <input class="button cancel" type="submit" value="<spring:message code="action.cancel"/>" />
	</div>


</form:form>

