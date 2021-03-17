<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<c:choose>
	<c:when test="${fn:length(videos) == 0}">
		<spring:message code="videos.empty" />
	</c:when>

	<c:otherwise>
		<ul class="items">
			<c:forEach items="${videos}" var="video">
				<li>
					<div class="item ">
						<a rel="internal" title="${video.displayTitle}"
							href="<c:url value="/video/show/${video.id}/" />"><c:out
								value="${video.displayTitle}" /></a>
					</div>
				</li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
