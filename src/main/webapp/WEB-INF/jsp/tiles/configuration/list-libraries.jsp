<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

	<c:choose>
		<c:when test="${fn:length(listLibrariesPage.libraries) == 0}">
			<spring:message code="list-libraries.library.empty" />
		</c:when>

		<c:otherwise>
			<ul class="main-menu">
				<c:forEach items="${listLibrariesPage.libraries}" var="library">
					<li><a class="library-${library.libraryTypeValue}"
						href="<c:url value="/app/configuration/music-library" />?id=<c:out value="${library.id}" />"><c:out value="${library.name}" /></a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</div>

<div class="button-panel">
	<a class="button" href="<c:url value="/app/configuration/choose-library-type" />"><spring:message code="list-libraries.library.add" /></a>
</div>
