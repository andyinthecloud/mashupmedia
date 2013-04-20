<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="sub-panel">

	<ul class="main-menu">
		<c:forEach items="${listRemoteLibrariesPage.remoteLibraries}"
			var="library">
			<li><c:out value="${library.name}" /></li>
		</c:forEach>
	</ul>

</div>

<div class="button-panel">
	<a class="button"
		href="<c:url value="/app/configuration/administration/add-user" />"><spring:message
			code="configuration.administration.list-remote-libraries.add-remote-library" /></a>
</div>