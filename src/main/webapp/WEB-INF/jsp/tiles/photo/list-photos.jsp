<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>

<c:choose>
	<c:when test="${fn:length(photos) == 0}">
		<spring:message code="list-photos.empty" />
	</c:when>
	<c:otherwise>
		<div class="photos">
			<c:forEach items="${photos}" var="photo">
				<div class="photo"><a href="<c:url value="/app/photo/show/${photo.id}/" />">
						<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
						src="<c:url value="/app/streaming/media/${photo.id}/thumbnail" />" />
				</a></div>
			</c:forEach>
		</div>
	</c:otherwise>
</c:choose>
