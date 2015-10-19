<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<!-- @IS_SHOWN_AFTER_FORM@ -->

<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
	<ul class="main-menu" data-role="listview">
		<li><a rel="internal"
			href="<c:url value="/app/configuration/network" />"
			title="<spring:message code="network.title" />"><spring:message
					code="configuration.menu.network" /></a></li>
		<li><a rel="internal"
			href="<c:url value="/app/configuration/list-libraries" />"
			title="<spring:message code="list-libraries.title" />"><spring:message
					code="configuration.menu.libraries" /></a></li>
		<li><a rel="internal"
			href="<c:url value="/app/configuration/administration/list-users" />"
			title="<spring:message code="configuration.administration.list-users.title" />"><spring:message
					code="configuration.menu.users" /></a></li>
		<li><a rel="internal"
			href="<c:url value="/app/configuration/administration/list-groups" />"
			title="<spring:message code="configuration.administration.list-groups.title" />"><spring:message
					code="configuration.menu.groups" /></a></li>
		<li><a href="<c:url value="/app/configuration/encoding" />"><spring:message
					code="configuration.menu.encoding" /></a></li>
		<li><a
			href="<c:url value="/app/configuration/list-remote-libraries" />"><spring:message
					code="configuration.menu.remotelibraries" /></a></li>
	</ul>


</sec:authorize>
