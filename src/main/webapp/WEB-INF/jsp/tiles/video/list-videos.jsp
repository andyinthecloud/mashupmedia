<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<div class="sub-panel">

	<c:choose>
		<c:when test="${fn:length(videos) == 0}">
			<spring:message code="list-videos.empty" />
		</c:when>

		<c:otherwise>
			<ul class="main-menu">
				<c:forEach items="${videos}" var="video">
					<li><a 
						href="<c:url value="/app/video/show/${video.id}/" />"><c:out value="${video.displayTitle}" /></a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</div>