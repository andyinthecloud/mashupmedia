<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>



<script type="text/javascript">
    $(document).ready(
                    function() {
                        $("form input").attr('autocomplete', 'off');

                        processEnabledFields();

                        $("div.ui-content form").on("change", "#user_enabled", function() {
                            processEnabledFields();
                        });

                        $("div.ui-content form").on("click", "#button-delete", function() {
                            $("#action").val("delete");
                            $(this).closest("form").submit();
                        });

                        $("div.ui-content form").on("click", "#button-cancel", function() {
                            window.location = "<c:url value="/app/configuration/administration/list-users" />";
                        });

                        $("#editUserPage").submit(
                                        function(event) {
                                            event.preventDefault();
                                            submitAjaxForm($(this), "<spring:message code ="configuration.administration.list-users.title" />",
                                                            "<c:url value="/app/configuration/administration/list-users" />");
                                        });

                    });

    function processEnabledFields() {

        var enabledCheckboxElement = $("#user_enabled");
        if (enabledCheckboxElement.length == 0) { return; }

        var isEnabled = false;

        if (enabledCheckboxElement.is(":checked")) {
            isEnabled = true;
        }

        var fieldContainerElement = $("div.is-toggle-disable");

        if (isEnabled) {
            $(fieldContainerElement).removeClass("ui-state-disabled");
        } else {
            $(fieldContainerElement).addClass("ui-state-disabled");
        }

    }
</script>


<c:url var="actionUrl"
	value="/app/configuration/administration/submit-user" />
<form:form commandName="editUserPage" action="${actionUrl}"
	autocomplete="false">

	<form:hidden path="action" />
	<form:hidden path="user.id" />
	<form:hidden path="user.editable" />

	<form:errors path="*" cssClass="error-box" />

	<c:choose>
		<c:when
			test="${editUserPage.user.editable}">
			<div class="new-line">
				<form:checkbox path="user.enabled" value="true" id="user_enabled" />
				<label for="user_enabled"><spring:message
						code="configuration.administration.edit-user.enabled" /></label>
			</div>

			<div class="new-line is-toggle-disable">
				<form:checkbox path="administrator" />
				<label for="administrator1"><spring:message
						code="configuration.administration.edit-user.administrator" /></label>
			</div>
		</c:when>

		<c:otherwise>
			<form:hidden path="user.enabled" />
			<form:hidden path="administrator" />
		</c:otherwise>
	</c:choose>

	<div class="new-line is-toggle-disable">
		<label><spring:message
				code="configuration.administration.edit-user.username" /></label>
		<form:input path="user.username" data-clear-btn="true" />
	</div>


	<div class="new-line is-toggle-disable">
		<label><spring:message
				code="configuration.administration.edit-user.name" /></label>
		<form:input path="user.name" autocomplete="false"
			data-clear-btn="true" />
	</div>

	<c:if
		test="${editUserPage.user.editable || editUserPage.showAdministrator == false}">
		<div class="new-line is-toggle-disable">
			<fieldset data-role="controlgroup">
				<legend>
					<spring:message
						code="configuration.administration.edit-user.groups" />
				</legend>
				<form:checkboxes path="user.groups" items="${groups}"
					itemLabel="name" itemValue="id" />
			</fieldset>
		</div>
	</c:if>

	<div class="new-line">
		<form:hidden path="user.password" />
		<fieldset>
			<legend>
				<spring:message
					code="configuration.administration.edit-user.change-password" />
			</legend>

			<div class="new-line is-toggle-disable">
				<label><spring:message
						code="configuration.administration.edit-user.password" /></label>
				<form:password path="newPassword" autocomplete="false" />
			</div>

			<div class="new-line is-toggle-disable">
				<label><spring:message
						code="configuration.administration.edit-user.repeat-password" /></label>
				<form:password path="newRepeatPassword" autocomplete="false" />
			</div>

		</fieldset>
	</div>


	<div>
		<c:if test="${editUserPage.user.id > 0 && editUserPage.user.editable}">
			<input id="button-delete" class="button" type="button"
				value="<spring:message code="action.delete"/>" />
		</c:if>

		<input class="button" type="submit"
			value="<spring:message code="action.save"/>" />
	</div>


</form:form>

