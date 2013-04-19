<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<ul class="main-menu">
	<li><a href="<c:url value="/app/configuration/network" />"><spring:message code="configuration.menu.network" /></a></li>
	<li><a href="<c:url value="/app/configuration/music-configuration" />"><spring:message code="configuration.menu.music" /></a></li>
	<li><a href="<c:url value="/app/configuration/administration/list-users" />"><spring:message code="configuration.menu.users" /></a></li>
	<li><a href="<c:url value="/app/configuration/administration/list-groups" />"><spring:message code="configuration.menu.groups" /></a></li>
	<li><a href="<c:url value="/app/configuration/encoding" />"><spring:message code="configuration.menu.encoding" /></a></li>
	<li><a href="<c:url value="/app/configuration/list-remote-libraries" />"><spring:message code="configuration.menu.remotelibraries" /></a></li>
</ul>


