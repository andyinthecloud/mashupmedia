<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<!-- @IS_SHOWN_AFTER_FORM@ -->

<ul data-role="listview" data-filter="true"
	data-filter-placeholder="<spring:message code="configuration.administration.list-users.search"/>"
	data-inset="true">

	<c:forEach items="${listGroupsPage.groups}" var="group">
		<li><a rel="internal"
			title="<spring:message code="configuration.administration.edit-group.title" />"
			href="<c:url value="/app/configuration/administration/edit-group/${group.id}" />"><c:out
					value="${group.name}" /></a></li>
	</c:forEach>

</ul>

<div class="new-line">
	<a class="ui-btn ui-btn-inline" rel="internal"
		title="<spring:message code="configuration.administration.edit-group.title" />"
		href="<c:url value="/app/configuration/administration/add-group" />"><spring:message
			code="configuration.administration.list-groups.add-group" /></a>
</div>

