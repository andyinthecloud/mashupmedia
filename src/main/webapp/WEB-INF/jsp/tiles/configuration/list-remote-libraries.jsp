<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="sub-panel">

	<ul class="main-menu">
		<c:forEach items="${listRemoteLibrariesPage.remoteLibraries}"
			var="library">
			<li><a href="<c:url value="/app/configuration/edit-remote-library?libraryId=${library.id}" />"><c:out value="${library.name}" /></a></li>
		</c:forEach>
	</ul>

</div>

<div class="button-panel">
	<a class="button"
		href="<c:url value="/app/configuration/edit-remote-library" />"><spring:message
			code="configuration.list-remote-libraries.add-remote-library" /></a>
</div>