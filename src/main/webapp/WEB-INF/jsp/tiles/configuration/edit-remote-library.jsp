<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<form:form commandName="editRemoteLibraryPage">
	<form:errors path="*" cssClass="error-box" />
	<form:hidden path="remoteLibrary.id" />

	<label for="remoteLibrary-name"><spring:message
			code="configuration.edit-remote-library.name" /></label>
	<form:input path="remoteLibrary.name" id="remoteLibrary-name" />
	<br />

	<form:checkbox path="remoteLibrary.enabled" id="remoteLibrary-enabled"
		cssStyle="vertical-align: middle;" />
	<label for="remoteLibrary-enabled"><spring:message
			code="configuration.edit-remote-library.enabled" /></label>
	<br />


	<label class="new-line" for="remoteLibrary-location-path"><spring:message
			code="configuration.edit-remote-library.url" /></label>
	<form:input path="remoteLibrary.location.path"
		id="remoteLibrary-location-path" />
	<br />

	<label class="new-line" for="groups"><spring:message
			code="configuration.edit-remote-library.groups" /></label>
	<form:checkboxes path="remoteLibrary.groups" items="${groups}"
		itemLabel="name" itemValue="id" cssClass="checkboxes"
		delimiter="<br/>" />
	<br />

	<div class="button-panel">
		<input class="button" name="save" type="submit"
			value="<spring:message code="action.save" />" />
		<c:if test="${remoteLibrary.id > 0}">
			<a class="button"
				href="<c:url value="/app/configuration/delete-remote-library?libraryId=${remoteLibrary.id}" />"><spring:message
					code="action.delete" /></a>
		</c:if>
	</div>
</form:form>