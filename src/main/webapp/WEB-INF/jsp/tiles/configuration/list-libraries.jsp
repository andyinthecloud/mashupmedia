<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<!-- @IS_SHOWN_AFTER_FORM@ -->

<c:choose>
	<c:when test="${fn:length(listLibrariesPage.libraries) == 0}">
		<div class="center-panel">
			<spring:message code="list-libraries.library.empty" />
		</div>
	</c:when>

	<c:otherwise>
		<ul class="main-menu ui-listview-inset ui-corner-all ui-shadow"
			data-role="listview">
			<c:forEach items="${listLibrariesPage.libraries}" var="library">
				<li><a class="library-${library.libraryTypeValue}"
					rel="internal" title="<spring:message code="library.music.title"/>"
					href="<c:url value="/configuration/library/${library.libraryTypeValue}/" />?id=<c:out value="${library.id}" />"><c:out
							value="${library.name}" /></a></li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>

<div class="new-line">
	<a class="ui-btn ui-btn-inline" rel="internal"
		title="<spring:message code="list-libraries.title" />"
		href="<c:url value="/configuration/choose-library-type" />"><spring:message
			code="list-libraries.library.add" /></a>
</div>
