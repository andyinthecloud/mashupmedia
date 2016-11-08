<%@ include file="/WEB-INF/jsp/inc/taglibs.jsp"%>


<div class="sub-panel">

	<c:choose>
		<c:when test="${fn:length(album.photos) == 0}">
			<spring:message code="list-photos.empty" />
		</c:when>
		<c:otherwise>
			<ul class="photo-thumbnails">
				<c:forEach items="${album.photos}" var="photo">
					<li><a href="<c:url value="/app/photo/show/${photo.id}?sequenceType=photo_album" />">
							<img alt="${photo.displayTitle}" title="${photo.displayTitle}"
							src="<c:url value="/app/photo/thumbnail/${photo.id}" />" />
					</a></li>
				</c:forEach>
			</ul>
		</c:otherwise>
	</c:choose>
</div>