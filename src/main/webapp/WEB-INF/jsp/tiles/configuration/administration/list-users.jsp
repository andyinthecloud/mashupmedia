<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<!-- @IS_SHOWN_AFTER_FORM@ -->

<ul data-role="listview" data-filter="true"
	data-filter-placeholder="<spring:message code="configuration.administration.list-users.search"/>"
	data-inset="true">

	<c:forEach items="${listUsersPage.users}" var="user">
		<li><a rel="internal"
			title="<spring:message code="configuration.administration.edit-user.title" />"
			href="<c:url value="/configuration/administration/edit-user/${user.id}" />"><c:out
					value="${user.name}" /></a></li>
	</c:forEach>
</ul>

<div class="new-line">
	<a class="ui-btn ui-btn-inline" rel="internal"
		title="<spring:message code="configuration.administration.edit-user.title" />"
		href="<c:url value="/configuration/administration/add-user" />"><spring:message
			code="configuration.administration.list-users.add-user" /></a>
</div>

