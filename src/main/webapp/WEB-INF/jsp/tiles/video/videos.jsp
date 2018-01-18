<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:choose>
	<c:when test="${fn:length(videos) == 0}">
		<spring:message code="videos.empty" />
	</c:when>

	<c:otherwise>
		<ul class="videos">
			<c:forEach items="${videos}" var="video">
				<li><a rel="internal" title="${video.displayTitle}"
					href="<c:url value="/app/video/show/${video.id}/" />"><c:out
							value="${video.displayTitle}" /></a></li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
