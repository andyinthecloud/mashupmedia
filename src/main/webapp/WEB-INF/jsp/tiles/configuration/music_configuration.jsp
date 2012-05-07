<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="panel">

	<c:choose>
		<c:when test="${fn:length(musicConfigurationPage.musicLibraries) == 0}">
			<spring:message code="musicconfiguration.library.empty" />
		</c:when>

		<c:otherwise>
			<ul>
				<c:forEach items="${musicConfigurationPage.musicLibraries}" var="musicLibrary">
					<li><a href="<c:url value="/app/configuration/music-library" />?id=<c:out value="${musicLibrary.id}" />"><c:out value="${musicLibrary.name}" /></a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
<br/>
<a href="<c:url value="/app/configuration/music-library" />"><spring:message code="musicconfiguration.library.add"/></a>

</div>