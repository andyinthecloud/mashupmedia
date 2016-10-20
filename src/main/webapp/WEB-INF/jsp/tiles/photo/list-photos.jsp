<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:choose>
	<c:when test="${fn:length(photos) == 0}">
		<spring:message code="list-photos.empty" />
	</c:when>
	<c:otherwise>
		<ul class="photos">
			<c:forEach items="${photos}" var="photo">
				<li class="photo"><a href="<c:url value="/app/photo/show/${photo.id}/" />">
						<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
						src="<c:url value="/app/streaming/media/${photo.id}/thumbnail" />" />
				</a></li>
			</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>
