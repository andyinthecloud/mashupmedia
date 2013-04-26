<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:url var="formUrl" value="/app/configuration/edit-remote-library"/>
<form:form commandName="editRemoteLibraryPage" action="${formUrl}">
	<form:errors path="*" cssClass="error-box" />

	<form:hidden path="libraryId" />
	<form:hidden path="libraryTypeValue"/>

	<form:checkbox path="enabled" cssStyle="vertical-align: middle;" />
	<label for="enabled"><spring:message code="configuration.edit-remote-library.enabled" /></label>
	<br />

	<label class="new-line" for="name"><spring:message code="configuration.edit-remote-library.name" /></label>
	<form:input path="name" />
	<br />



	<label class="new-line" for="url"><spring:message code="configuration.edit-remote-library.url" /></label>
	<form:input path="url"/>
	<br />

	<label class="new-line" for="groups"><spring:message code="configuration.edit-remote-library.groups" /></label>
	<form:checkboxes path="groups" items="${groups}" itemLabel="name" itemValue="id" cssClass="checkboxes" delimiter="<br/>" />
	<br />

	<div class="button-panel">
		<input class="button" name="save" type="submit" value="<spring:message code="action.save" />" />
		<c:if test="${editRemoteLibraryPage.libraryId > 0}">
			<a class="button" href="<c:url value="/app/configuration/delete-remote-library?libraryId=${editRemoteLibraryPage.libraryId}" />"><spring:message code="action.delete" /></a>
		</c:if>
	</div>
</form:form>
